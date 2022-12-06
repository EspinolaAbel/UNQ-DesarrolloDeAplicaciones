package ar.edu.unq.desapp.grupoN.desapp.service.job

import ar.edu.unq.desapp.grupoN.desapp.configuration.COIN_PRICE_CACHE
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrice
import ar.edu.unq.desapp.grupoN.desapp.service.CryptoExchangeService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.cache.CacheManager

@Component
class CacheUpdaterJob(
    val cacheManager: CacheManager,
    val cryptoService: CryptoExchangeService
) : Job {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext?) {
        val cache = cacheManager.getCache<Symbol, CoinPrice>(COIN_PRICE_CACHE)
        cache.removeAll()
        val values = cryptoService.getPrices(Symbol.values())
        values.forEach { price -> cache.put(Symbol.valueOf(price.symbol), price) }
        logger.info("cache $COIN_PRICE_CACHE was updated")
    }
}