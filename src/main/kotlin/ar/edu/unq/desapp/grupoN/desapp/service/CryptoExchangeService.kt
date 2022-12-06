package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.*
import ar.edu.unq.desapp.grupoN.desapp.model.mapping.AdvertisementMapper
import ar.edu.unq.desapp.grupoN.desapp.persistence.AdvertisementRepository
import ar.edu.unq.desapp.grupoN.desapp.persistence.OperationRepository
import ar.edu.unq.desapp.grupoN.desapp.service.client.BcraClient
import ar.edu.unq.desapp.grupoN.desapp.service.client.BinanceClient
import ar.edu.unq.desapp.grupoN.desapp.service.dto.*
import ar.edu.unq.desapp.grupoN.desapp.service.exception.CryptoExchangeException
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.transaction.Transactional
import kotlin.math.abs

@Service
class CryptoExchangeService(
    val advertisementRepository: AdvertisementRepository,
    val advertisementMapper: AdvertisementMapper,
    val operationRepository: OperationRepository,
    val userService: UserService,
    val binanceClient: BinanceClient,
    val bcraClient: BcraClient,
) {

    companion object {
        const val RANGE_PERCENTAGE = 5.0
    }

    fun getPrice(symbol: Symbol): CoinPrice {
        return binanceClient.getPrice(symbol)
    }

    fun getPrices(values: Array<Symbol>): List<CoinPrice> {
        return binanceClient.getPrices(values)
    }

    fun getSymbolPriceLast24hr(symbol: Symbol): CoinPrices {
        return binanceClient.getSymbolPriceLast24hr(symbol)
    }

    fun createAdvertisement(dto: CreateAdvertisementDTO): Advertisement {
        val ad = advertisementMapper.toNewModel(dto)
        ad.user = userService.findUserOrThrow(dto.userId)
        ad.active = true
        if (!validateCryptoPriceIsInRange(dto.symbol, dto.cryptoPrice))
            throw CryptoExchangeException.advertisementPriceIsOutOfRange(dto.symbol, dto.cryptoPrice, RANGE_PERCENTAGE)
        return advertisementRepository.save(ad)
    }

    private fun validateCryptoPriceIsInRange(symbol: Symbol, price: Double): Boolean {
        val currentPrice = getPrice(symbol).price
        val priceDifference = abs(price - currentPrice.value)
        return priceDifference <= (currentPrice.value * RANGE_PERCENTAGE / 100)
    }

    fun getAdvertisements(userId: Int): UserAdvertisementsResponse {
        val user = userService.findUserOrThrow(userId)
        return UserAdvertisementsResponse(user, advertisementRepository.findActiveAdvertisements(userId))
    }

    fun getAdvertisement(adUUID: UUID): Optional<AdvertisementResponseDTO> {
        return advertisementRepository.findById(adUUID)
            .filter(Objects::nonNull)
            .map{ model -> advertisementMapper.toFullDto(model)}
            .map{ dto: AdvertisementResponseDTO ->
                try {
                    val fiatExchangeRate = bcraClient.getCurrentArPesoUsDollarExchangeRate().sellPrice()
                    setFiatPrice(dto, fiatExchangeRate!!)
                } catch (_: Exception) { }
                dto
            }
    }

    private fun setFiatPrice(dto: AdvertisementResponseDTO, exchangeRate: Double) {
       dto.fiatPrice = CurrencyAmount(CurrencyCode.ARS,
           dto.cryptoPrice.value * dto.cryptoAmount * exchangeRate
       )
    }
    //private fun setFiatPrice(dto: UserAdvertisementResponseDTO, exchangeRate: Double) {
        //dto.fiatPrice = dto.cryptoPrice * dto.cryptoAmount * exchangeRate
    //}

    @Transactional
    fun createOperation(dto: OperationRequestDTO): Operation {
        val adUUID: UUID = dto.advertisement!!
        val ad = advertisementRepository.findActiveAndNotInUse(adUUID)
            .orElseThrow {
                throw CryptoExchangeException.advertisementNotFoundOrInUse(adUUID)
            }
        val user = userService.findUserOrThrow(dto.userId)
        val operation = Operation(null, user, ad)
        return operationRepository.save(operation)
    }

    fun getOperations(userId: Int): UserOperationsResponse {
        val user = userService.findUserOrThrow(userId)
        return UserOperationsResponse(user, operationRepository.findActiveOperations(userId))
    }

    fun getOperation(opUUID: UUID): Optional<Operation> {
        return operationRepository.findById(opUUID)
    }

    @Transactional
    fun updateOperation(opUUID: UUID, newStatus: OperationStatus, userId: Int): OperationUpdate {
        val op = findOperationOrThrow(opUUID)
        val userCaller = getUserFromOperation(op, userId)
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
        if (op.wasSuccessfullyCompleted()) {
            val points = if (op.duration().toMinutes() <= 30) 10 else 5
            userService.updateUserPunctuationOperation(op.user.id!!, points, true)
            userService.updateUserPunctuationOperation(op.advertisement.user?.id!!, points, true)
        }
        else if (op.wasCancelled()) {
            val points = -20
            userService.updateUserPunctuationOperation(op.user.id!!, points, false)
        }
    }

    private fun findOperationOrThrow(opUUID: UUID): Operation = opUUID.let { operationRepository.findById(it) }
        .orElseThrow { CryptoExchangeException.operationDoesNotExists(opUUID) }!!

    fun getUserOperationsVolume(userId: Int): UserOperationsVolumeResponseDTO {
        val cryptoVolumes: List<UserCryptoVolume> = operationRepository.userVolume(userId).stream()
            .map { v ->
                val currentCryptoPrice = binanceClient.getPrice(v.getCrypto()).price.value
                val arsUsd = bcraClient.getCurrentArPesoUsDollarExchangeRate().sellPrice()
                UserCryptoVolume(
                    v.getCrypto(),
                    v.getNominalAmount(),
                    CurrencyAmount(CurrencyCode.USD, currentCryptoPrice),
                    CurrencyAmount(CurrencyCode.ARS, currentCryptoPrice * arsUsd!! )
                )
            }
            .toList()
        var totalValueArs = 0.0
        var totalValueUsd = 0.0
        cryptoVolumes.forEach{ ucv ->
            totalValueArs += ucv.currentPriceArs.value
            totalValueUsd += ucv.currentPriceUsd.value
        }
        return UserOperationsVolumeResponseDTO(
            Instant.now(),
            setOf(
                CurrencyAmount(CurrencyCode.USD, totalValueUsd),
                CurrencyAmount(CurrencyCode.ARS, totalValueArs)
            ),
            cryptoVolumes
        )
    }

}

class OperationUpdate(op: Operation, user: User) {
    val crypto: Symbol
    val quantity: Double
    val price: CurrencyAmount
    var total: CurrencyAmount?
    val userId: Int
    val userName: String
    val userOperations: Int
    val points: Int
    val destinationAddress: String
    val action: String

    init {
        crypto = op.advertisement.symbol
        quantity = op.advertisement.cryptoAmount
        price = op.advertisement.cryptoPrice
        total = CurrencyAmount(price.currency, quantity * price.value)
        userId = op.user.id!!
        userName = "${op.user.name} ${op.user.lastName}"
        userOperations = user.closedOperations
        points = user.points
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

open class UserActivityResponse(user: User) {
    val userName: String = "${user.name} ${user.lastName}"
    val reputation: Any = if (user.reputation > 0) user.reputation else "Sin operaciones"
    val closedOperations: Int = user.closedOperations
}

class UserAdvertisementsResponse(
    @JsonIgnore val user: User,
    val advertisements: List<AdvertisementView>,
): UserActivityResponse(user)

class UserOperationsResponse(
    @JsonIgnore val user: User,
    val operations: List<OperationView>,
): UserActivityResponse(user)
