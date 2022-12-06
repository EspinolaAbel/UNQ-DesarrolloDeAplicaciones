package ar.edu.unq.desapp.grupoN.desapp.service.dto

import ar.edu.unq.desapp.grupoN.desapp.model.OperationType
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.Positive

open class CreateAdvertisementDTO(
    @JsonIgnore
    var userId: Int,
    @field:NotNull
    var operationType: OperationType,
    @field:NotNull
    var symbol: Symbol,
    @field:NotNull @field:Positive
    var cryptoAmount: Double,
    @field:NotNull @field:Positive
    var cryptoPrice: Double
)