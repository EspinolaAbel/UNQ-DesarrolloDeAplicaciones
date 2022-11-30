package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import ar.edu.unq.desapp.grupoN.desapp.model.OperationType
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.util.UUID

class AdvertisementResponseDTO(
    var id: UUID,
    var userId: Int,
    var operationType: OperationType,
    var symbol: Symbol,
    var cryptoAmount: Double,
    var cryptoPrice: CurrencyAmount,
) {
    var fiatPrice: CurrencyAmount? = null
}