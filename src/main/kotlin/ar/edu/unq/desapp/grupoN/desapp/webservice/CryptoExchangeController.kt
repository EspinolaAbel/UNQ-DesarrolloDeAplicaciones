package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.configuration.JwtUtil
import ar.edu.unq.desapp.grupoN.desapp.model.*
import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus.*
import ar.edu.unq.desapp.grupoN.desapp.model.dto.*
import ar.edu.unq.desapp.grupoN.desapp.service.CryptoExchangeService
import ar.edu.unq.desapp.grupoN.desapp.service.UserAdvertisementsResponse
import ar.edu.unq.desapp.grupoN.desapp.service.UserOperationsResponse
import ar.edu.unq.desapp.grupoN.desapp.service.aop.LogExecutionTime
import ar.edu.unq.desapp.grupoN.desapp.service.exception.CryptoExchangeException
import ar.edu.unq.desapp.grupoN.desapp.service.exception.UserApiException
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.PropertySource
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

    @ApiOperation(value = "\${cryp-exch.get-symbol-price.value}", notes = "\${cryp-exch.get-symbol-price.notes}")
    @GetMapping("{symbol}/price")
    fun getSymbolPrice(@PathVariable symbol: Symbol): ApiResponse<CoinPrice> {
        return ApiResponse(service.getPrice(symbol))
    }

    @LogExecutionTime
    @ApiOperation(value = "\${cryp-exch.get-symbol-price-24h.value}", notes = "\${cryp-exch.get-symbol-price-24h.notes}")
    @GetMapping("{symbol}/price/24h")
    fun getSymbolPriceLast24hr(@PathVariable symbol: Symbol): ApiResponse<CoinPrices> {
        return ApiResponse(service.getSymbolPriceLast24hr(symbol))
    }

    @ApiOperation(value = "\${cryp-exch.create-advertisement.value}", notes = "\${cryp-exch.create-advertisement.notes}")
    @PostMapping("p2p/advertisements")
    fun createAdvertisement(@Valid @RequestBody adDto: CreateAdvertisementDTO): ResponseEntity<ApiResponse<String>> {
        return try {
            adDto.userId = getUserIdFromToken()
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

    @ApiOperation(value = "\${cryp-exch.get-advertisement.value}", notes = "\${cryp-exch.get-advertisement.notes}")
    @GetMapping("p2p/advertisements/{adUUID}")
    fun getAdvertisement(@PathVariable adUUID: UUID): ResponseEntity<ApiResponse<AdvertisementResponseDTO>> {
        return service.getAdvertisement(adUUID)
            .map { o -> ApiResponse(o) }
            .map { r -> ResponseEntity.ok(r) }
            .orElse(ResponseEntity.notFound().build())
    }

    @ApiOperation(value = "\${cryp-exch.get-advertisements.value}", notes = "\${cryp-exch.get-advertisements.notes}")
    @GetMapping("p2p/advertisements")
    fun getAdvertisements(): ApiResponse<UserAdvertisementsResponse> {
        val userId = getUserIdFromToken()
        return ApiResponse(service.getAdvertisements(userId))
    }

    @ApiOperation(value = "\${cryp-exch.create-operation.value}", notes = "\${cryp-exch.create-operation.notes}")
    @PostMapping("p2p/operations")
    fun createOperation(@Valid @RequestBody dto: OperationRequestDTO): ResponseEntity<ApiResponse<Operation>> {
        return try {
            dto.userId = getUserIdFromToken()
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

    @ApiOperation(value = "\${cryp-exch.get-operations.value}", notes = "\${cryp-exch.get-operations.notes}")
    @GetMapping("p2p/operations")
    fun getOperations(): ApiResponse<UserOperationsResponse> {
        val userId = getUserIdFromToken()
        return ApiResponse(service.getOperations(userId))
    }

    @ApiOperation(value = "\${cryp-exch.get-operation.value}", notes = "\${cryp-exch.get-operation.notes}")
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

    @ApiOperation(value = "\${cryp-exch.update-operation.value}", notes = "\${cryp-exch.update-operation.notes}")
    @PatchMapping("p2p/operations/{opUUID}/{updateStatus}")
    fun updateOperation(
        @PathVariable opUUID: UUID,
        @PathVariable updateStatus: OperationUpdateStatus
    ): ResponseEntity<ApiResponse<Any>> {
        return try {
            val userId = getUserIdFromToken()
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

    @ApiOperation(value = "\${cryp-exch.user-volume-operations.value}", notes = "\${cryp-exch.user-volume-operations.notes}")
    @GetMapping("p2p/operations/user-volume")
    fun userOperationsVolume(@RequestParam userId: Int): ApiResponse<UserOperationsVolumeResponseDTO> {
        return ApiResponse(service.getUserOperationsVolume(userId))
    }

    private fun getUserIdFromToken() = JwtUtil.getToken().orElseThrow().id

}