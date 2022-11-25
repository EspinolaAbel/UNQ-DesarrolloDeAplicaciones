package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.time.Instant
import java.util.*

interface OperationView {

    fun getTimestamp(): Instant
    fun getCrypto(): Symbol
    fun getAmount(): Double
    fun getPrice(): Double
    fun getUserName(): String
    fun getOperations(): Int
    fun getReputation(): String
    fun getAdvertisement(): UUID

}