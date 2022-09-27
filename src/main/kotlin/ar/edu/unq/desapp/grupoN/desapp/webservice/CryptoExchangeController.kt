package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.model.SymbolsEnum
import ar.edu.unq.desapp.grupoN.desapp.service.CryptoExchangeService
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrice
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("cryptos")
@RestController
class CryptoExchangeController(val service: CryptoExchangeService) {

    @GetMapping("price/{symbol}")
    fun getSymbolPrice(@PathVariable symbol: SymbolsEnum): CoinPrice {
        return service.getPrice(symbol)
    }

    @GetMapping("price/{symbol}/24h")
    fun getSymbolPriceLast24hr(@PathVariable symbol: SymbolsEnum): CoinPrices {
        return service.getSymbolPriceLast24hr(symbol)
    }

}