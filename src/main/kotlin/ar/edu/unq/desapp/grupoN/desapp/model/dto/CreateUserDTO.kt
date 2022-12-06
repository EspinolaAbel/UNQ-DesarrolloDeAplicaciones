package ar.edu.unq.desapp.grupoN.desapp.model.dto

import ar.edu.unq.desapp.grupoN.desapp.model.validation.UserPassword
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class CreateUserDTO(
    @field:NotBlank @field:Size(min=3, max=30)
    var name: String,
    @field:NotBlank @field:Size(min=3, max=30)
    var lastName: String,
    @field:NotBlank @field:Email
    var email: String,
    @field:NotBlank @field:Size(min=10, max=30)
    var address: String,
    @field:NotBlank @field:UserPassword
    var password: String,
    @field:NotBlank @field:Pattern(regexp="^\\d{22}$", message = "Debe tener 22 números")
    var cvu: String,
    @field:NotBlank @field:Pattern(regexp="^\\d{8}$", message = "Debe tener 8 números")
    var walletAddress: String) {

}
