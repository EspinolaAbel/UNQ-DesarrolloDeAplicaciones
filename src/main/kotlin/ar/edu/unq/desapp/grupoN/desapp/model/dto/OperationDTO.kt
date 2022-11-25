package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.OperationType
import java.util.*
import javax.validation.constraints.NotNull

data class OperationDTO(
    var id: UUID?,
    /** el usuario que está ejerciendo la operación */
    @NotNull
    val userId: Int, // TODO: este lo puedo obtener desde JWT cuando tenga implemantada la autenticación
    @NotNull
    var advertisement: UUID?,
    @NotNull
    val price: Double,
    @NotNull
    val operation: OperationType) {
}
