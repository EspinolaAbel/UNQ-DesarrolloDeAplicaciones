package ar.edu.unq.desapp.grupoN.desapp.configuration.jwt

import ar.edu.unq.desapp.grupoN.desapp.model.User
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class JwtUtil(@Value("\${app.security.secret}") val secret: String? = null) {

    @Throws(IllegalArgumentException::class, JWTCreationException::class)
    fun generateToken(user: User): String {
        return JWT.create()
            .withPayload(mapOf("id" to user.id, "email" to user.email))
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusSeconds(3600))
            .sign(Algorithm.HMAC256(secret))
    }

    @Throws(JWTVerificationException::class)
    fun validateTokenAndRetrieveSubject(token: String?): String {
        val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(secret))
            .build()
        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.getClaim("email").asString()
    }

    companion object {
        fun getToken(): Optional<UserToken> {
            val authentication = SecurityContextHolder.getContext().authentication ?: return Optional.empty()
            val token: String? = (authentication.details as Map<String,String>)["token"]
            val payload: Payload = JWT.decode(token)
            val id: String = payload.claims["id"].toString()
            if (!(""+id).matches(Regex("^\\d+$")))
                return Optional.empty()
            val userId: Int = Integer.parseInt(payload.claims["id"].toString())
            return Optional.ofNullable(UserToken(userId))
        }
    }

}

data class UserToken(val id: Int);