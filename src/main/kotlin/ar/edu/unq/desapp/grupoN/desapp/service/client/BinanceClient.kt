package ar.edu.unq.desapp.grupoN.desapp.service.client

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import ar.edu.unq.desapp.grupoN.desapp.model.dto.BinanceCoinPrice
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrice
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import ar.edu.unq.desapp.grupoN.desapp.model.dto.PriceWithDatetime
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Instant
import java.time.temporal.ChronoUnit

/** <a href="https://binance-docs.github.io/apidocs/spot/en/#introduction">Binance API Docs</a> */
@Component
class BinanceClient(
    val restTemplate: RestTemplate = RestTemplate(),
    @Value("\${app.api.binance.url}")
    val baseUrl: String
) {

    @Cacheable(cacheNames = ["CoinPriceCache"])
    fun getPrice(symbol: Symbol): CoinPrice {
        val res: BinanceCoinPrice = restTemplate.getForObject(baseUrl + "v3/ticker/price?symbol=${symbol}")
        return CoinPrice(res.symbol, CurrencyAmount(CurrencyCode.USD, res.price))
    }

    @Cacheable(cacheNames = ["CoinPricesCache"])
    fun getSymbolPriceLast24hr(symbol: Symbol): CoinPrices {
        val startTime = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli()
        val interval = "15m"
        val url = baseUrl + "v3/klines?symbol=${symbol}&interval=${interval}&startTime=${startTime}"

        val last24hCandles = restTemplate.getForObject<List<List<Any>>>(url)

        val last24hPrices = last24hCandles.stream()
            .map { c -> PriceWithDatetime((c[4] as String).toDouble(), Instant.ofEpochMilli(c[6] as Long) ) }
            .toList()
        return CoinPrices(symbol.name, last24hPrices, CurrencyCode.USD)
    }

}