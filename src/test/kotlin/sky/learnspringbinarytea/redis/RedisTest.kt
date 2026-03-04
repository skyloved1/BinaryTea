package sky.learnspringbinarytea.redis

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import kotlin.test.assertEquals

@SpringBootTest
class RedisTest {
    @Autowired
    lateinit var redisTemplate: RedisTemplate<Any, Any>
    @Test
    fun contextLoads() {
        redisTemplate.opsForValue().set("test", 5)
        val value = redisTemplate.opsForValue().get("test")
        println("Value from Redis: $value")
        assertEquals(5, value)
    }
}