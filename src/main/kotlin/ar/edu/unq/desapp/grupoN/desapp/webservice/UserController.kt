package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.service.dto.CreateUserDTO
import ar.edu.unq.desapp.grupoN.desapp.model.User
import ar.edu.unq.desapp.grupoN.desapp.service.dto.ApiResponse
import ar.edu.unq.desapp.grupoN.desapp.service.UserService
import ar.edu.unq.desapp.grupoN.desapp.service.exception.UserApiException
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.validation.Valid

@RequestMapping("users")
@RestController
class UserController(private val service: UserService) {

    @ApiOperation(value = "\${user.save-user.value}", notes = "\${user.save-user.notes}")
    @PostMapping
    fun saveUser(@Valid @RequestBody user: CreateUserDTO): ResponseEntity<Any> {
        try {
            val u = User(
                null,
                user.name,
                user.lastName,
                user.email,
                user.address,
                user.password,
                user.cvu,
                user.walletAddress
            )
            val newUser = service.saveUser(u);
            return ResponseEntity.created(URI("user/${newUser.id}")).build();
        }
        catch (e: UserApiException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message));
        }
    }

    @ApiOperation(value = "\${user.list-users.value}", notes = "\${user.list-users.notes}")
    @GetMapping
    fun getUsers(): ApiResponse<List<User>> {
        return ApiResponse(service.getUsers())
    }

    @ApiOperation(value = "\${user.get-user.value}", notes = "\${user.get-user.notes}")
    @GetMapping("{id}")
    fun getUser(@PathVariable id: Int): ResponseEntity<ApiResponse<User>> {
        val optUser = service.findUser(id);
        return if (optUser.isPresent)
            ResponseEntity.ok().body(ApiResponse(optUser.get()))
        else
            ResponseEntity.notFound().build()
    }

}