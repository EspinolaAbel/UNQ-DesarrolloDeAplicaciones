package ar.edu.unq.desapp.grupoN.desapp.configuration

import ar.edu.unq.desapp.grupoN.desapp.webservice.UserController
import ar.edu.unq.desapp.grupoN.desapp.service.dto.CreateUserDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct

@Configuration
@PropertySource("classpath:apirestdocs.properties")
class DesAppConfiguration(val om: ObjectMapper, val env: Environment, val userController: UserController) {

    @PostConstruct
    fun init () {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @EventListener
    fun afterRefresh(event: ApplicationStartedEvent) {
        if (!env.activeProfiles.contains("development"))
            return

        for (i in 1..3) {
            var user = CreateUserDTO(
                "Username"+i,
                "Lastname"+i,
                "user${i}@mail.com",
                "address ${i} user${i}",
                "abc123D%",
                "%022d".format(i),
                "%08d".format(i)
            )
            userController.saveUser(user)
        }
    }

}