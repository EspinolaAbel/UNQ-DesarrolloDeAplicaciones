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
import java.text.NumberFormat
import java.util.*

/** <a href="https://www.dolarsi.com/api/api.php?type=valoresprincipales">BCRA API Docs</a> */
@Component
class BcraClient(
    val restTemplate: RestTemplate = RestTemplate(),
    @Value("\${app.api.bcra.url}")
    val baseUrl: String
) {
    val listEntryTypeReference = object: ParameterizedTypeReference<List<Entry>>() {}

    @Cacheable(cacheNames = ["UsdEntryCache"])
    fun getCurrentArPesoUsDollarExchangeRate(): Casa {
        val headers = HttpHeaders()
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, listEntryTypeReference).body!!
        return response.stream()
            .map { entry -> entry.casa }
            .filter { casa -> "DOLAR OFICIAL".equals(""+(casa.nombre)!!.trim().uppercase(Locale.getDefault()))}
            .findFirst()
            .orElseThrow()
    }

}

data class Entry(
    var casa: Casa,
): Serializable {
}

data class Casa(
    var compra: String?,
    var venta: String?,
    var agencia: String?,
    var nombre: String?,
    var variacion: String?,
    var ventaCero: String?,
    var decimales: String?,
): Serializable {
    fun buyPrice(): Double? = toDoubleLocale(venta)
    fun sellPrice(): Double? = toDoubleLocale(compra)
    private fun toDoubleLocale(value: String?): Double? {
        if (value == null)
            return null;
        val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-AR"))
        numberFormat.maximumFractionDigits = 2
        return numberFormat.parse(value).toDouble()
    }
}

