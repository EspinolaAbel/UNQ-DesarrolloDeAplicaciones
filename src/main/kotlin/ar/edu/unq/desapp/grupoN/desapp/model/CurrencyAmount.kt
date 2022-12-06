package ar.edu.unq.desapp.grupoN.desapp.model

import java.io.Serializable
import java.text.DecimalFormat
import javax.persistence.Embeddable
import kotlin.math.roundToInt

@Embeddable
data class CurrencyAmount(
    val currency: CurrencyCode,
    var value: Double,
): Serializable {
    init {
        value = (value * 100).roundToInt().toDouble() / 100
    }
}

enum class CurrencyCode {
    ARS, USD
}
