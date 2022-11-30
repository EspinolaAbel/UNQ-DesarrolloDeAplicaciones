package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.model.*
import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus.*
import ar.edu.unq.desapp.grupoN.desapp.model.dto.*
import ar.edu.unq.desapp.grupoN.desapp.service.CryptoExchangeService
import ar.edu.unq.desapp.grupoN.desapp.service.UserActivityResponse
import ar.edu.unq.desapp.grupoN.desapp.service.UserAdvertisementsResponse
import ar.edu.unq.desapp.grupoN.desapp.service.UserOperationsResponse
import ar.edu.unq.desapp.grupoN.desapp.service.exception.CryptoExchangeException
import ar.edu.unq.desapp.grupoN.desapp.service.exception.UserApiException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import javax.validation.Valid

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
    fun createAdvertisement(@Valid @RequestBody adDto: CreateAdvertisementDTO): ResponseEntity<ApiResponse<String>> {
        return try {
            val ad = service.createAdvertisement(adDto)
            val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ad.id).toUri();
            ResponseEntity.created(location).build()
        }
        catch (e: Exception) {
            when (e) {
                is CryptoExchangeException, is UserApiException -> {
                    val errors = mapOf<String, String>("user" to "" + e.message)
                    ResponseEntity.badRequest().body(ApiResponse(null, errors))
                }
                else -> throw e
            }
        }
    }

    @GetMapping("p2p/advertisements/{adUUID}")
    fun getAdvertisement(@PathVariable adUUID: UUID): ResponseEntity<ApiResponse<AdvertisementResponseDTO>> {
        return service.getAdvertisement(adUUID)
            .map { o -> ApiResponse(o) }
            .map { r -> ResponseEntity.ok(r) }
            .orElse(ResponseEntity.notFound().build())
    }

    //@GetMapping("p2p/advertisements")
    //fun getAdvertisements(@RequestParam(required = false, defaultValue = "false") findAll: Boolean): ApiResponse<List<UserAdvertisementResponseDTO>> {
    //    return ApiResponse(service.getAdvertisements(findAll))
    //}

    @GetMapping("p2p/advertisements")
    fun getAdvertisements(@RequestParam userId: Int): ApiResponse<UserAdvertisementsResponse> {
        return ApiResponse(service.getAdvertisements(userId))
    }

    @PostMapping("p2p/operations")
    fun createOperation(@Valid @RequestBody dto: OperationRequestDTO): ResponseEntity<ApiResponse<Operation>> {
        return try {
            val op = service.createOperation(dto)
            val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(op.id).toUri();
            ResponseEntity.created(location).body(ApiResponse(op))
        }
        catch (e: CryptoExchangeException) {
            val m = mapOf("message" to ""+e.message)
            ResponseEntity.badRequest().body(ApiResponse(null, m))
        }
    }

    @GetMapping("p2p/operations")
    fun getOperations(@RequestParam userId: Int): ApiResponse<UserOperationsResponse> {
        return ApiResponse(service.getOperations(userId))
    }

    @GetMapping("p2p/operations/{opUUID}")
    fun getOperation(@PathVariable opUUID: UUID): Any? {
        return service.getOperation(opUUID)
            .map { o -> ApiResponse(o) }
            .map { r -> ResponseEntity.ok(r) }
            .orElse(ResponseEntity.notFound().build())
    }

    enum class OperationUpdateStatus(val operationStatus: OperationStatus) {
        userdeposit(INTERESTED_USER_DEPOSIT),
        completed(COMPLETED),
        cancel(CANCELLED),
    }

    @PatchMapping("p2p/operations/{opUUID}/{updateStatus}")
    fun updateOperation(
        @PathVariable opUUID: UUID,
        @PathVariable updateStatus: OperationUpdateStatus,
        @RequestParam userId: Int // TODO: borrar cuando tenga andando spring security
    ): ResponseEntity<ApiResponse<Any>> {
        return try {
            val body = service.updateOperation(opUUID, updateStatus.operationStatus, userId)
            ResponseEntity.ok(ApiResponse(body))
        }
        catch (e: Exception) {
            when (e) {
                is CryptoExchangeException, is OperationException ->
                    ResponseEntity.badRequest().body(ApiResponse(null, mapOf("error" to e.message!!)))
                else -> throw e
            }
        }
    }

    @GetMapping("p2p/operations/user-volume")
    fun userOperationsVolume(@RequestParam userId: Int): ApiResponse<UserOperationsVolumeResponseDTO> {
        return ApiResponse(service.getUserOperationsVolume(userId))
    }

}