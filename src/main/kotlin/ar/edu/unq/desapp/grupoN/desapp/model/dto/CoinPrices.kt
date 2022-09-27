package ar.edu.unq.desapp.grupoN.desapp.model.dto

class CoinPrices(val symbol: String, val prices: List<PriceWithDatetime> = ArrayList()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoinPrices

        if (symbol != other.symbol) return false
        if (prices != other.prices) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + prices.hashCode()
        return result
    }

    override fun toString(): String {
        return "CoinPrices(symbol='$symbol', prices=$prices)"
    }
}