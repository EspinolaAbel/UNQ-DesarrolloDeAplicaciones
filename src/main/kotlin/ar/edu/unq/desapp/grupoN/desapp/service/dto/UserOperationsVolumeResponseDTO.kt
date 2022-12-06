package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.time.Instant

/**
◦ Dia y hora de solicitud
◦ Valor total operado en dólares
◦ Valor total operado en pesos ARG
◦ Activos:
    ▪ Criptoactivo
    ▪ Cantidad nominal del Cripto Activo
    ▪ Cotización actual del Cripto Activo
    ▪ Monto de la cotización en pesos ARG
 */
data class UserOperationsVolumeResponseDTO(
    val timestamp: Instant,
    val totalValue: Set<CurrencyAmount>,
    val assets: List<UserCryptoVolume>
)

class UserCryptoVolume(
    val crypto: Symbol,
    val nominalAmount: Double,
    val currentPriceUsd: CurrencyAmount,
    val currentPriceArs: CurrencyAmount
)

interface CryptoVolumeView {
    fun getCrypto(): Symbol
    fun getNominalAmount(): Double
}