package sky.learnspringbinarytea.controllertest

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import tools.jackson.databind.ObjectMapper
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class OrderControllerTest {

    var jdbcClient: JdbcClient? = null

    var mockMvc: MockMvc? = null

    @Autowired
    lateinit var objectMapperProvider: ObjectProvider<ObjectMapper>

    @BeforeEach
    fun contextLoads(wac: WebApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply<DefaultMockMvcBuilder>(springSecurity()).build()
        jdbcClient = JdbcClient.create(wac.getBean<DataSource>())
    }


    @AfterEach
    fun teardown() {
        mockMvc = null
        jdbcClient = null
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
            mockMvc!!.perform(logout())
                .andExpectAll(
                    status().is3xxRedirection,
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

    //Token测试
    @Test
    fun testOrderPageWithTokenAuthentication() {
        val token = context(mockMvc!!) {
            getToken(
                username = "lilei",
                password = "binarytea"
            ).getOrNull()
        }

//        println("mockMvc=" + System.identityHashCode(mockMvc))
//        val auth = Base64.getEncoder().encodeToString("lilei:binarytea".toByteArray())
//        mockMvc!!.perform(get("/order").header("Authorization", "Basic $auth"))
//            .andDo(print())

        //手动 buildRequest(it)，会绕开/破坏 RequestPostProcessor 的正常链路。
        // 因此，使用 httpBasic() 时，不要手动 buildRequest，而是直接传递 httpBasic() 生成的 RequestPostProcessor。
        mockMvc!!
            .perform(
                get("/order")
                    .header("Authorization", "Bearer $token")
            )
            .andExpectAll(
                status().is2xxSuccessful
            )

    }

    context(mockMvc: MockMvc)
    private fun getToken(username: String, password: String): Result<String> {
        runCatching {
            mockMvc
                .perform(
                    post("/token").header("Authorization", "Bearer ").contentType("application/json").content(
                        objectMapperProvider.getIfAvailable { ObjectMapper() }.writeValueAsString(
                            mapOf(
                                "username" to username,
                                "password" to password
                            )
                        )
                    )
                )
                .andExpect(status().isOk)
                .andReturn().response.contentAsString.let { str ->
                    objectMapperProvider.getIfAvailable { ObjectMapper() }.readTree(str)
                }.get("token").asString().also {
                    println("token: $it")
                }
        }.onSuccess {
            return Result.success(it)
        }.onFailure {
            println("Failed to get token: ${it.message}")
            return Result.failure(it)
        }
        return Result.failure(UnknownError("Failed to get token"))
    }

    //HttpBasic测试
    @Test
    fun testOrderPageWithHttpBasicAuthentication() {
        mockMvc!!
            .perform(
                get("/order")
                    .with(httpBasic("lilei", "binarytea"))
                    .header("Accept", "text/html")
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

    @Test
    fun testOrderPageWithPersistentToken() {
        mockMvc!!.perform(
            post("/login")
                .param("username", "lilei")
                .param("password", "binarytea")
                .param("remember-me", "1")
        ).andExpectAll(
            status().is3xxRedirection,
            redirectedUrl("/order"),
            authenticated()
        ).andDo {
            assertNotNull(it.response.getCookie("remember-me"), "remember-me cookie should be present")
        }

        val count = jdbcClient!!.sql("select count(*) from persistent_logins where username = :username")
            .param("username", "lilei")
            .query(Int::class.java)
            .single()
        assertTrue(count > 0, "persistent_logins should contain token row for lilei")

        mockMvc!!.perform(logout())
            .andExpectAll(
                status().is3xxRedirection,
                redirectedUrl("/login"),
                unauthenticated()
            ).andDo {
                assertTrue(
                    it.response.getCookie("remember-me")?.maxAge == 0 || it.response.getHeader("Expires") == "Thu, 01 Jan 1970",
                    "remember-me cookie should be deleted"
                )
            }
    }

}
