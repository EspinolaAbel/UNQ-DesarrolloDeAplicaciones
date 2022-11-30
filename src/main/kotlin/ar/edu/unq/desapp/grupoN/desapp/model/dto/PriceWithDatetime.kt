package ar.edu.unq.desapp.grupoN.desapp.model.dto

import java.io.Serializable
import java.time.Instant

class PriceWithDatetime(val price: Double, val datetime: Instant): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PriceWithDatetime

        if (price != other.price) return false
        if (datetime != other.datetime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = price.hashCode()
        result = 31 * result + datetime.hashCode()
        return result
    }

    override fun toString(): String {
        return "PriceWithDatetime(price=$price, datetime=$datetime)"
    }
}