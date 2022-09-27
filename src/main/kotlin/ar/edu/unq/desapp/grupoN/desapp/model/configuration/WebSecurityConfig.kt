package ar.edu.unq.desapp.grupoN.desapp.model.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests -> requests
                .antMatchers("api/swagger-resources/**", "api/swagger-ui/**", "api/v2/api-docs").permitAll()
                //.anyRequest().authenticated()
            }
            .csrf { csrf -> csrf.disable() }
            .formLogin { form -> form.permitAll() }
            .logout { logout -> logout.permitAll() }
            .httpBasic { }
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.withDefaultPasswordEncoder()
            .username("root")
            .password("root")
            .roles("USER", "ADMIN")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}