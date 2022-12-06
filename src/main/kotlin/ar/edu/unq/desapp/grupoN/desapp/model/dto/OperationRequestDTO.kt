package ar.edu.unq.desapp.grupoN.desapp.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.validation.constraints.NotNull

data class OperationRequestDTO(
    /** el usuario que está ejerciendo la operación */
    @JsonIgnore
    var userId: Int,
    @field:NotNull
    var advertisement: UUID?) {
}
