package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.*
import ar.edu.unq.desapp.grupoN.desapp.model.dto.*
import ar.edu.unq.desapp.grupoN.desapp.model.mapping.AdvertisementMapper
import ar.edu.unq.desapp.grupoN.desapp.persistence.AdvertisementRepository
import ar.edu.unq.desapp.grupoN.desapp.persistence.OperationRepository
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.CryptoExchangeException
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.UserApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.transaction.Transactional

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
        ad.user = findUserOrThrow(dto.userId)
        return advertisementRepository.save(ad)
    }

    private fun findUserOrThrow(userId: Int): User = userService.getUser(userId)
        .orElseThrow { UserApiException.userWithIdDoesNotExists(userId) }

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
    fun createOperation(dto: OperationDTO): Operation {
        val adId: UUID? = dto.advertisement;

        val adAlreadyHasAnOperation = operationRepository.existsByAdvertisement_IdAndStatus(adId, OperationStatus.IN_PROGRESS)
        if (adAlreadyHasAnOperation)
            throw CryptoExchangeException.advertisementAlreadyInAnOperation(adId)

        val ad = findAdvertisementOrThrow(dto.advertisement)
        val user = findUserOrThrow(dto.userId)
        val operation = Operation(dto.id, user, ad, dto.price, dto.operation, Instant.now())
        return operationRepository.save(operation)
    }

    fun getOperations(): List<OperationView> {
        return operationRepository.findAllProjection()
    }

    fun getOperation(opUUID: UUID): Optional<Operation> {
        return operationRepository.findById(opUUID)
    }

}