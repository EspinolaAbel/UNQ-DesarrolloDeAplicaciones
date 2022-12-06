package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.time.Instant

data class UserAdvertisementResponseDTO(
    var creationTimestamp: Instant,
    var symbol: Symbol,
    var cryptoAmount: Double,
    var cryptoPrice: CurrencyAmount,
    var fiatPrice: CurrencyAmount,
    var userId: Int,
    var userName: String,
    var operations: Int,
    var reputation: Any,
)