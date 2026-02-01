package sky.learnspringbinarytea

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import sky.learnspringbinarytea.Config.BinaryTeaProperties

@SpringBootTest
class LearnSpringBinaryTeaApplicationTests {
    companion object{
        val logger: Logger = LoggerFactory.getLogger(LearnSpringBinaryTeaApplicationTests::class.java)
    }
    @Test
    fun contextLoads() {
    }

    @Test
    fun  testProperties(ctx: ApplicationContext) {
        val properties = ctx.getBean<BinaryTeaProperties>()
        logger.info("Binary Tea Ready: ${properties.ready}")
        logger.info("Binary Tea Open Hours: ${properties.openHours}")

    }
}
