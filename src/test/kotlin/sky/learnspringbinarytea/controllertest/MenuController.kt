package sky.learnspringbinarytea.controllertest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestPropertySource(properties = ["spring.devtools.restart.enabled=false"])
class MenuController {
    lateinit var mock: MockMvc

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @BeforeEach
    fun contextLoads(webContext: WebApplicationContext) {
        mock = MockMvcBuilders.webAppContextSetup(webContext)
            .alwaysExpect<DefaultMockMvcBuilder>(status().isOk())
            .build()
    }

    @Test
    fun testGetAll() {
        mock.get("/menu")
            .andExpect { status().isOk }
            .andExpect { content().contentType(MediaType.APPLICATION_JSON) }
            .andExpectAll {
                jsonPath("$").isArray
            }
            .andReturn().response.contentAsString.also {
                JacksonJsonParser().parseList(it).forEach(::println)
            }
    }

    @Test
    fun testGetById() {
        mock.perform(get("/menu/1"))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                handler().methodName("getById"),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("Java咖啡")
            )
    }
    @Test
    @Transactional
    fun `should create new menu item`() {


        mock.perform(post("/menu")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "Latte")
            .param("size", "小杯")
            .param("price",  "20.99")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Latte"))
            .andExpect(jsonPath("$.price.amount").value(20.99))
    }
}
