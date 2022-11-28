package ar.edu.unq.desapp.grupoN.desapp.model.dto

import java.util.*
import javax.validation.constraints.NotNull

data class OperationRequestDTO(
    /** el usuario que está ejerciendo la operación */
    @field:NotNull
    val userId: Int, // TODO: este lo puedo obtener desde JWT cuando tenga implemantada la autenticación
    @field:NotNull
    var advertisement: UUID?) {
}
