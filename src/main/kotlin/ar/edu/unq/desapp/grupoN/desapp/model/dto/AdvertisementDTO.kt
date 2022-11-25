package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.OperationType
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.Positive

open class AdvertisementDTO(
    @NotNull
    var userId: Int,
    @NotNull
    var operationType: OperationType,
    @NotNull
    var symbol: Symbol,
    @NotNull @Positive
    var cryptoAmount: Double,
    @NotNull @Positive
    var cryptoPrice: Double,
    @NotNull @Positive
    var fiatPrice: Double
) {
}