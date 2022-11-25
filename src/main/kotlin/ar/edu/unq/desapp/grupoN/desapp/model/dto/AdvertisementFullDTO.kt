package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.OperationType
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.util.UUID

class AdvertisementFullDTO(
    var id: UUID,
    userId: Int,
    operationType: OperationType,
    symbol: Symbol,
    cryptoAmount: Double,
    cryptoPrice: Double,
    fiatPrice: Double,
) : AdvertisementDTO(userId, operationType, symbol, cryptoAmount, cryptoPrice, fiatPrice) {
}