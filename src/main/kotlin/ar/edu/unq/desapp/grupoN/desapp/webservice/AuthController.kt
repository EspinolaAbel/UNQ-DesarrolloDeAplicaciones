package ar.edu.unq.desapp.grupoN.desapp.webservice

import ar.edu.unq.desapp.grupoN.desapp.configuration.JwtUtil
import ar.edu.unq.desapp.grupoN.desapp.persistence.UserRepository
import ar.edu.unq.desapp.grupoN.desapp.webservice.dto.LoginCredentialsRequest
import ar.edu.unq.desapp.grupoN.desapp.webservice.dto.LoginCredentialsResponse
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.annotation.PostConstruct


@RestController
@RequestMapping("auth")
class AuthController(
    val userRepo: UserRepository,
    val authManager: AuthenticationManager,
    val jwtUtil: JwtUtil
) {
    @GetMapping("login")
    fun get() { }

    @ApiOperation(value = "\${auth.user.value}", notes = "\${auth.user.notes}")
    @PostMapping("login")
    fun loginHandler(@RequestBody body: LoginCredentialsRequest): ResponseEntity<LoginCredentialsResponse> {
        return try {
            val user = userRepo.findByEmail(body.user)
                .orElseThrow{ UsernameNotFoundException(body.user) }
            val authInputToken = UsernamePasswordAuthenticationToken(body.user, body.password)
            SecurityContextHolder.getContext().authentication = authInputToken
            authManager.authenticate(authInputToken)
            val token: String = jwtUtil.generateToken(user)
            ResponseEntity.ok(LoginCredentialsResponse(token))
        } catch (authExc: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

}
