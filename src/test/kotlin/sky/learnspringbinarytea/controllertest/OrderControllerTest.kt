package sky.learnspringbinarytea.controllertest

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import tools.jackson.databind.ObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class OrderControllerTest {
    var mockMvc: MockMvc? = null

    @Autowired
    lateinit var objectMapperProvider: ObjectProvider<ObjectMapper>

    @BeforeEach
    fun contextLoads(wac: WebApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply<DefaultMockMvcBuilder>(springSecurity()).build()
    }


    @AfterEach
    fun teardown() {
        mockMvc = null
    }

    @Test
    @Order(2)
    fun testLogin() {
        runCatching {
            mockMvc!!.perform {
                formLogin("/login")
                    .user("lilei")
                    .password("binarytea")
                    .buildRequest(it)
            }
                .andExpectAll(
                    authenticated(),
                    redirectedUrl("/order")
                )
                .andDo { result ->
                    result.response.headerNames.forEach(::println)
                    result.response.cookies.forEach(::println)
                }

        }.also { assertTrue(it.isSuccess) }
    }

    @Test
    @Order(3)
    fun testLogout() {
        runCatching {
            mockMvc!!.perform {
                logout().buildRequest(it)
            }
                .andExpectAll(
                    unauthenticated(),
                    redirectedUrl("/login")
                )

        }.also { assertTrue(it.isSuccess) }
    }

    @Test
    @Order(1)
    fun testUnauthorizedAccess() {
        runCatching {
            mockMvc!!.perform {
                formLogin()
                    .user("invalidUser")
                    .password("invalidPassword")
                    .buildRequest(it)
            }
                .andExpectAll(
                    unauthenticated(),
                    {
                        assertEquals(expected = "application/json;charset=UTF-8", actual = it.response.contentType)
                        //测试是否有Refresh头
                        assertNotNull(it.response.getHeader("Refresh"))
                        //测试响应体
                        val objectMapper = objectMapperProvider.getIfAvailable { ObjectMapper() }
                        it.response.contentAsString.let { str ->
                            objectMapper.readTree(str)
                        }.apply {
                            assertEquals(expected = 401, actual = get("status").asInt())
                            assertEquals(expected = "Unauthorized", actual = get("error").asString())
                        }


                    }
                )
        }.onFailure {
            println(it.message)
        }
    }

    @Test
    fun testOrderPageWithAuthenticatedUser() {
        mockMvc!!.perform(
            get("/order").with(user("lilei").password("binarytea"))
        ).andExpectAll(
            status().is2xxSuccessful
        )

//        println("mockMvc=" + System.identityHashCode(mockMvc))
//        val auth = Base64.getEncoder().encodeToString("lilei:binarytea".toByteArray())
//        mockMvc!!.perform(get("/order").header("Authorization", "Basic $auth"))
//            .andDo(print())

        //手动 buildRequest(it)，会绕开/破坏 RequestPostProcessor 的正常链路。
        // 因此，使用 httpBasic() 时，不要手动 buildRequest，而是直接传递 httpBasic() 生成的 RequestPostProcessor。
        mockMvc!!.perform(
            get("/order")
                .with(httpBasic("lilei", "binarytea"))
        )
            .andExpectAll(
                status().is2xxSuccessful
            )
    }
    @Test
    fun testOrderPageWithUnauthorizedAccess() {
        mockMvc!!.perform(get("/order").header("Accept", "text/html"))//模拟浏览器
        .andExpectAll(
            status().is3xxRedirection, //浏览器是否跳转到401
            redirectedUrlPattern("/**/login")
        )
    }
}
