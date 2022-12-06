package ar.edu.unq.desapp.grupoN.desapp.integration

import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(properties = [
    //"logging.level.web=TRACE",
    //"logging.level.org.springframework.web=TRACE"
])
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired private lateinit var mockMvc: MockMvc

    private var token: String = ""
    private val om: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun getToken(): Unit {
        val r = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"user\": \"user1@mail.com\", \"password\": \"abc123D%\" }")
        )
            .andExpect(status().isOk)
            .andReturn()
        token = om.readTree(r.response.contentAsString).get("accessToken")
                .textValue()
    }

    @Test
    fun getUserTest() {
        val r = mockMvc.perform(
                get("/users/1")
                .header("Authorization", "Bearer $token")
            )
            .andExpect(status().isOk)
            .andReturn()
        val data = om.readTree(r.response.contentAsString).get("data")
        assertTrue(data.get("id").isInt)
        assertEquals("Username1", data.get("name").textValue())
        assertEquals("Lastname1", data.get("lastName").textValue())
    }

}