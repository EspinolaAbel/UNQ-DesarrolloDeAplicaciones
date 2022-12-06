package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import java.io.Serializable

data class CoinPrices(
    val symbol: String,
    val prices: List<PriceWithDatetime> = ArrayList(),
    val currency: CurrencyCode
): Serializable