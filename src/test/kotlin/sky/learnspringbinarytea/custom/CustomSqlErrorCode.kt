package sky.learnspringbinarytea.custom

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Connection
import java.sql.SQLException
import kotlin.test.assertTrue

@SpringBootTest
class CustomSqlErrorCodeTest
    (

) {
   @Autowired lateinit var ctx: ApplicationContext
    @Autowired lateinit var jdbcTemplate: JdbcTemplate
    @Test
    fun test() {
        val customSQLErrorTranslator = ctx.getBean<CustomSQLErrorTranslator>()
        jdbcTemplate.exceptionTranslator = customSQLErrorTranslator
        try {
            jdbcTemplate.execute { connection: Connection ->
                connection.createStatement().use {
                    throw SQLException("CustomSQL error", "Nothing", 123456)
                }
            }
        } catch (e: Exception) {
            assertTrue { e is CustomSQLException }
            println((e as CustomSQLException).message)
        }
    }
}
