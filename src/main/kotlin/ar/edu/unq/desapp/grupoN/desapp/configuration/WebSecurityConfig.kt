package ar.edu.unq.desapp.grupoN.desapp.configuration

import ar.edu.unq.desapp.grupoN.desapp.configuration.jwt.JwtAuthEntryPoint
import ar.edu.unq.desapp.grupoN.desapp.configuration.jwt.JwtFilter
import ar.edu.unq.desapp.grupoN.desapp.persistence.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*


@Configuration
@EnableWebSecurity
class WebSecurityConfig(val usersRepo: UserRepository, val authEntryPoint: JwtAuthEntryPoint) {
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(authEntryPoint)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers(
                    "/api/auth/*", "/auth/*",
                    "/api/error",
                    "/api/swagger-resources/**", "/api/swagger-ui/**", "/api/v2/api-docs", "/api/swagger-ui/index.html"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic { }
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService =
        UserDetailsService { username -> usersRepo
            .findByEmail(username)
            .map { user ->
                org.springframework.security.core.userdetails.User(
                    user.email,
                    user.password,
                    Collections.singletonList(SimpleGrantedAuthority("ROLE_USER"))//AppUserDetails(user)
                )
            }
            .orElseThrow { UsernameNotFoundException(String.format("User: %s, not found", username) ) }
        }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

}