package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.SymbolsEnum
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrice
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import ar.edu.unq.desapp.grupoN.desapp.model.dto.PriceWithDatetime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Instant
import java.time.temporal.ChronoUnit

/** <a href="https://binance-docs.github.io/apidocs/spot/en/#introduction">Binance API Docs</a> */
@Service
class CryptoExchangeService(
        val restTemplate: RestTemplate = RestTemplate(),
        @Value("\${app.api.binance.url}") val baseUrl: String
    ) {

    fun getPrice(symbol: SymbolsEnum): CoinPrice {
        return restTemplate.getForObject<CoinPrice>(baseUrl + "v3/ticker/price?symbol=${symbol}")
    }

    fun getSymbolPriceLast24hr(symbol: SymbolsEnum): CoinPrices {
        val startTime = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli()
        val interval = "15m"
        val url = baseUrl + "v3/klines?symbol=${symbol}&interval=${interval}&startTime=${startTime}"

        var last24hCandles = restTemplate.getForObject<List<List<Any>>>(url)

        val last24hPrices = last24hCandles.stream()
            .map { c -> PriceWithDatetime((c[4] as String).toDouble(), Instant.ofEpochMilli(c[6] as Long) ) }
            .toList()
        return CoinPrices(symbol.name, last24hPrices)
    }

}