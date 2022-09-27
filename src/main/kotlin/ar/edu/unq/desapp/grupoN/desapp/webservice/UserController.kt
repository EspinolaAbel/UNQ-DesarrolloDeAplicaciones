package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.model.dto.UserDTO
import ar.edu.unq.desapp.grupoN.desapp.persistence.entity.User
import ar.edu.unq.desapp.grupoN.desapp.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.validation.Valid

@RequestMapping("users")
@RestController
class UserController(private val service: UserService) {

    @PostMapping
    fun saveUser(@Valid @RequestBody user: UserDTO): ResponseEntity<URI> {
        val u = User(null, user.name, user.lastName, user.email, user.address, user.password, user.cvu, user.walletAddress)
        var newUser = service.saveUser(u);
        return ResponseEntity.created(URI("user/${newUser.id}")).build();
    }

    @GetMapping
    fun getUsers(): List<User> {
        return service.getUsers()
    }

    @GetMapping("{id}")
    fun getUser(@PathVariable id: Int): Optional<User> {
        return service.getUser(id);
    }

}