package sky.learnspringbinarytea

import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class DataSourceDemoApplicationTests(
    val ctx: ApplicationContext,
    @Value("\${spring.datasource.url}") val jdbcUrl: String,
) {


    @Test
    fun testDatasource() {
        assertTrue { ctx.containsBean("dataSource") }
        val ds = ctx.getBean<DataSource>();
        assertTrue { ds is HikariDataSource }

        val hikariDs = ds as HikariDataSource
        assertEquals(jdbcUrl, hikariDs.jdbcUrl)
        assertEquals(20, hikariDs.maximumPoolSize)
        assertEquals(10, hikariDs.minimumIdle)
        assertEquals("com.mysql.cj.jdbc.Driver", hikariDs.driverClassName)

        val connection = hikariDs.connection
        assertNotNull(connection)
        connection.close()
    }
}