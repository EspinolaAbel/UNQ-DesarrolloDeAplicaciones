package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.*
import ar.edu.unq.desapp.grupoN.desapp.model.dto.*
import ar.edu.unq.desapp.grupoN.desapp.model.mapping.AdvertisementMapper
import ar.edu.unq.desapp.grupoN.desapp.persistence.AdvertisementRepository
import ar.edu.unq.desapp.grupoN.desapp.persistence.OperationRepository
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.CryptoExchangeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.transaction.Transactional
import kotlin.math.abs

/** <a href="https://binance-docs.github.io/apidocs/spot/en/#introduction">Binance API Docs</a> */
@Service
class CryptoExchangeService(
    val restTemplate: RestTemplate = RestTemplate(),
    val advertisementRepository: AdvertisementRepository,
    val advertisementMapper: AdvertisementMapper,
    val operationRepository: OperationRepository,
    val userService: UserService,
    @Value("\${app.api.binance.url}")
    val baseUrl: String
) {

    companion object {
        const val RANGE_PERCENTAGE = 5.0
    }

    fun getPrice(symbol: Symbol): CoinPrice {
        return restTemplate.getForObject(baseUrl + "v3/ticker/price?symbol=${symbol}")
    }

    fun getSymbolPriceLast24hr(symbol: Symbol): CoinPrices {
        val startTime = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli()
        val interval = "15m"
        val url = baseUrl + "v3/klines?symbol=${symbol}&interval=${interval}&startTime=${startTime}"

        val last24hCandles = restTemplate.getForObject<List<List<Any>>>(url)

        val last24hPrices = last24hCandles.stream()
            .map { c -> PriceWithDatetime((c[4] as String).toDouble(), Instant.ofEpochMilli(c[6] as Long) ) }
            .toList()
        return CoinPrices(symbol.name, last24hPrices)
    }

    fun createAdvertisement(dto: AdvertisementDTO): Advertisement {
        val ad = advertisementMapper.toNewModel(dto)
        ad.user = userService.findUserOrThrow(dto.userId)
        ad.active = true
        // TODO: descomentar cuando tenga la cache andando
        //if (!validateCryptoPriceIsInRange(dto.symbol, dto.cryptoPrice, RANGE_PERCENTAGE))
        //    throw CryptoExchangeException.advertisementPriceIsOutOfRange(dto.symbol, dto.cryptoPrice, rangePercentage)
        return advertisementRepository.save(ad)
    }

    private fun validateCryptoPriceIsInRange(symbol: Symbol, price: Double, rangePercentage: Double): Boolean {
        val currentPrice = getPrice(symbol).price
        val priceDifference = abs(price - currentPrice)
        return priceDifference <= (currentPrice * rangePercentage / 100)
    }


    private fun findAdvertisementOrThrow(adId: UUID?): Advertisement = adId?.let { advertisementRepository.findById(it) }
        ?.orElseThrow { CryptoExchangeException.advertisementDoesNotExists(adId) }!!

    fun getAdvertisements(findAll: Boolean): List<AdvertisementFullDTO> {
        val ads = if (findAll)
            advertisementRepository.findAll()
        else
            advertisementRepository.findAvailable()

        return ads.stream()
            .map{ model -> advertisementMapper.toFullDto(model) }
            .toList()
    }

    fun getAdvertisement(adUUID: UUID): Optional<AdvertisementFullDTO> {
        return advertisementRepository.findById(adUUID)
            .filter(Objects::nonNull)
            .map{ model -> advertisementMapper.toFullDto(model)}
    }

    @Transactional
    fun createOperation(dto: OperationRequestDTO): Operation {
        val adUUID: UUID = dto.advertisement!!;
        val ad = advertisementRepository.findActiveAndNotInUse(adUUID)
            .orElseThrow {
                throw CryptoExchangeException.advertisementNotFoundOrInUse(adUUID)
            }
        val user = userService.findUserOrThrow(dto.userId)
        val operation = Operation(null, user, ad)
        return operationRepository.save(operation)
    }

    fun getOperations(): List<OperationView> {
        return operationRepository.findAllProjection()
    }

    fun getOperation(opUUID: UUID): Optional<Operation> {
        return operationRepository.findById(opUUID)
    }

    @Transactional
    fun updateOperation(opUUID: UUID, newStatus: OperationStatus, userId: Int): OperationUpdate {
        val op = findOperationOrThrow(opUUID)
        val userCaller = getUserFromOperation(op, userId)
        // TODO: ver diferencia de precios
        op.updateStatus(userId, newStatus)
        if (op.isClosed())
            updateUsersStatistics(op)
        return OperationUpdate(operationRepository.save(op), userCaller)
    }

    private fun getUserFromOperation(op: Operation, userId: Int): User {
        return if (op.user.id == userId)
            op.user
        else if (op.advertisement.user?.id == userId)
            op.advertisement.user!!
        else
            throw RuntimeException("userId=$userId is not in operation ${op.id}")
    }

    private fun updateUsersStatistics(op: Operation) {
        var points = 0
        if (op.wasSuccessfullyCompleted()) {
            points = if (op.duration().toMinutes() <= 30) 10 else 5
            userService.updateUserPunctuationOperation(op.user.id!!, points, true)
            userService.updateUserPunctuationOperation(op.advertisement.user?.id!!, points, true)
        }
        else if (op.wasCancelled()) {
            points = -20
            userService.updateUserPunctuationOperation(op.user.id!!, points, false)
        }
    }

    private fun findOperationOrThrow(opUUID: UUID): Operation = opUUID.let { operationRepository.findById(it) }
        .orElseThrow { CryptoExchangeException.operationDoesNotExists(opUUID) }!!

}

class OperationUpdate {
    val crypto: Symbol
    val quantity: Double
    val price: Double
    val total: Double
    val userId: Int
    val userName: String
    val userOperations: Int
    val reputation: Int
    val destinationAddress: String
    val action: String

    constructor (op: Operation, user: User) {
        crypto = op.advertisement.symbol
        quantity = op.advertisement.cryptoAmount
        price = op.advertisement.cryptoPrice
        total = -1.0 // TODO: en pesos
        userId = op.user.id!!
        userName = "${op.user.name} ${op.user.lastName}"
        userOperations = user.closedOperations
        reputation = user.reputation
        destinationAddress = when (op.advertisement.operationType) {
            OperationType.BUY -> op.advertisement.user!!.cvu
            OperationType.SELL -> op.advertisement.user!!.walletAddress
        }
        action = when (op.status) {
            OperationStatus.STARTED -> ""
            OperationStatus.INTERESTED_USER_DEPOSIT -> "Realicé la transferencia"
            OperationStatus.COMPLETED -> "Confirmar recepción"
            OperationStatus.CANCELLED -> "Cancelar"
        }
    }

}
