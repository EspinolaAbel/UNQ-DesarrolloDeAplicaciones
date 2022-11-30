package ar.edu.unq.desapp.grupoN.desapp.model

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class CurrencyAmount(
    val currency: CurrencyCode,
    val value: Double,
): Serializable

enum class CurrencyCode {
    ARS, USD
}
