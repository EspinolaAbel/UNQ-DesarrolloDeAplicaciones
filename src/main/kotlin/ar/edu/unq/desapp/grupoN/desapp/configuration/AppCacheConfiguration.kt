package ar.edu.unq.desapp.grupoN.desapp.configuration

import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrice
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import ar.edu.unq.desapp.grupoN.desapp.service.client.UsdEntry
import org.ehcache.config.CacheConfiguration
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import org.ehcache.jsr107.Eh107Configuration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import javax.cache.CacheManager
import javax.cache.Caching
import javax.cache.spi.CachingProvider


@Configuration
@EnableCaching
class AppCacheConfiguration {

    @Bean
    fun ehcacheManager(): CacheManager? {
        val provider: CachingProvider = Caching.getCachingProvider()
        val cacheManager: CacheManager = provider.cacheManager

        CacheBuilder<SimpleKey,UsdEntry>("UsdEntryCache", provider, cacheManager)
            .keyType(SimpleKey::class.java).valueType(UsdEntry::class.java)
            .build()

        CacheBuilder<Symbol,CoinPrice>("CoinPriceCache", provider, cacheManager)
            .keyType(Symbol::class.java).valueType(CoinPrice::class.java)
            .build()

        CacheBuilder<Symbol, CoinPrices>("CoinPricesCache", provider, cacheManager)
            .keyType(Symbol::class.java).valueType(CoinPrices::class.java)
            .build()

        return cacheManager
    }

    private class CacheBuilder<K,V>(val name: String, val provider: CachingProvider, val cacheManager: CacheManager) {
        var keyType: Class<K>? = null
        var valueType: Class<V>? = null
        var memorySize: Long = 10
        var expire: Long = 3600

        fun keyType(type: Class<K>): CacheBuilder<K,V> { keyType = type; return this; }
        fun valueType(type: Class<V>): CacheBuilder<K,V> { valueType = type; return this; }
        fun memorySize(memSize: Long): CacheBuilder<K,V> { memorySize = memSize; return this; }
        fun secondsToExpire(exp: Long): CacheBuilder<K,V> { expire = exp; return this; }

        fun build() {
            val cacheConfig: CacheConfiguration<K,V> = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyType, valueType,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .offheap(memorySize, MemoryUnit.MB)
                        .build()
                )
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(expire)))
                .build()

            val conf: javax.cache.configuration.Configuration<K,V> =
                Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig)
            cacheManager.createCache(name, conf)
        }

    }

}
