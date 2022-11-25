package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.model.Operation
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import ar.edu.unq.desapp.grupoN.desapp.model.dto.*
import ar.edu.unq.desapp.grupoN.desapp.service.CryptoExchangeService
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.CryptoExchangeException
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.UserApiException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import kotlin.properties.Delegates

@RequestMapping("cryptos")
@RestController
class CryptoExchangeController(
        val service: CryptoExchangeService
    ) {

    @GetMapping("{symbol}/price")
    fun getSymbolPrice(@PathVariable symbol: Symbol): ApiResponse<CoinPrice> {
        return ApiResponse(service.getPrice(symbol))
    }

    @GetMapping("{symbol}/price/24h")
    fun getSymbolPriceLast24hr(@PathVariable symbol: Symbol): ApiResponse<CoinPrices> {
        return ApiResponse(service.getSymbolPriceLast24hr(symbol))
    }

    @PostMapping("p2p/advertisements")
    fun createAdvertisement(@RequestBody adDto: AdvertisementDTO): ResponseEntity<ApiResponse<String>> {
        return try {
            val ad = service.createAdvertisement(adDto)
            val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ad.id).toUri();
            ResponseEntity.created(location).build()
        }
        catch (e: UserApiException) {
            val errors = mapOf<String,String>("user" to ""+e.message)
            ResponseEntity.badRequest().body(ApiResponse(null, errors))
        }
    }

    @GetMapping("p2p/advertisements/{adUUID}")
    fun getAdvertisement(@PathVariable adUUID: UUID): ResponseEntity<ApiResponse<AdvertisementFullDTO>> {
        return service.getAdvertisement(adUUID)
            .map { o -> ApiResponse(o) }
            .map { r -> ResponseEntity.ok(r) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("p2p/advertisements")
    fun getAdvertisements(@RequestParam(required = false, defaultValue = "false") findAll: Boolean): ApiResponse<List<AdvertisementFullDTO>> {
        return ApiResponse(service.getAdvertisements(findAll))
    }

    @PostMapping("p2p/operations")
    fun createOperation(@RequestBody dto: OperationDTO): Any? {
        return try {
            val op = service.createOperation(dto)
            val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(op.id).toUri();
            OperationUpdateResponse.of(op)
        } catch (e: CryptoExchangeException) {
            val m = mapOf("message" to ""+e.message)
            ResponseEntity.badRequest().body(ApiResponse(null, m))
        }
    }

    @GetMapping("p2p/operations")
    fun getOperations(): List<OperationView> {
        return service.getOperations()
    }

    @GetMapping("p2p/operations/{opUUID}")
    fun createOperation(@PathVariable opUUID: UUID): Any? {
        return service.getOperation(opUUID)
            .map { o -> ApiResponse(o) }
            .map { r -> ResponseEntity.ok(r) }
            .orElse(ResponseEntity.notFound().build())
    }

    class OperationUpdateResponse {
        var quantity by Delegates.notNull<Double>()
        lateinit var crypto: Symbol

        companion object {
            fun of(op: Operation): OperationUpdateResponse {
                val result = OperationUpdateResponse()
                result.crypto = op.advertisement.symbol
                result.quantity = op.advertisement.cryptoAmount
                return result
            }
        }
    }

}