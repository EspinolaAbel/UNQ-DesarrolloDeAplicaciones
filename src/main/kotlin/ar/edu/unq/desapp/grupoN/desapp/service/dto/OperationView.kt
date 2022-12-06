package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.time.Instant
import java.util.*

interface OperationView {

    fun getCreationTimestamp(): Instant
    fun getUpdateTimestamp(): Instant
    fun getCrypto(): Symbol
    fun getAmount(): Double
    fun getPrice(): CurrencyAmount
    fun getId(): UUID

}