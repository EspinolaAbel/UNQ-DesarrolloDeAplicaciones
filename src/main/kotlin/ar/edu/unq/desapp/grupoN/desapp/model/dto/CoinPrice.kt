package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import java.io.Serializable

data class CoinPrice(var symbol: String, var price: CurrencyAmount): Serializable

data class BinanceCoinPrice(var symbol: String, var price: Double): Serializable