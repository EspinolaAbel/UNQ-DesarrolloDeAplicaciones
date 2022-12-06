package ar.edu.unq.desapp.grupoN.desapp.configuration

import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtFilter(
    val userDetailsService: UserDetailsService,
    val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val jwt = authHeader.substring(7)
            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header")
            } else {
                try {
                    val email = jwtUtil.validateTokenAndRetrieveSubject(jwt)
                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(email)
                    val authToken =
                        UsernamePasswordAuthenticationToken(email, userDetails.password, userDetails.authorities)
                    authToken.details = mapOf("token" to jwt)
                    if (SecurityContextHolder.getContext().authentication == null) {
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                } catch (exc: JWTVerificationException) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token")
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
