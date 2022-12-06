package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.time.Instant
import java.util.UUID

interface AdvertisementView {

    fun getCreationTimestamp(): Instant
    fun getCrypto(): Symbol
    fun getAmount(): Double
    fun getPrice(): CurrencyAmount
    fun getId(): UUID

}