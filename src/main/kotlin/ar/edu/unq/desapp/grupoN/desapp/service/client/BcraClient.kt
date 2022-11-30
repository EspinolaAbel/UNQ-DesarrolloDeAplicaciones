package ar.edu.unq.desapp.grupoN.desapp.service.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.io.Serializable

/** <a href="https://estadisticasbcra.com/api/documentacion">BCRA API Docs</a> */
@Component
class BcraClient(
    val restTemplate: RestTemplate = RestTemplate(),
    @Value("\${app.api.bcra.url}")
    val baseUrl: String,
    @Value("\${app.api.bcra.token}")
    val token: String
) {
    val listUsdEntryTypeReference = object: ParameterizedTypeReference<List<UsdEntry>>() {}

    @Cacheable(cacheNames = ["UsdEntryCache"])
    fun getCurrentArPesoUsDollarExchangeRate(): UsdEntry {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        val entity = HttpEntity<Void>(headers)
        val l = restTemplate.exchange(baseUrl + "usd_of", HttpMethod.GET, entity, listUsdEntryTypeReference).body!!
        return l[l.size-1]
    }

}

data class UsdEntry(var d: String, val v: Double): Serializable

