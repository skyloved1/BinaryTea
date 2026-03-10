package sky.learnspringbinarytea.redis

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size
import kotlin.test.assertEquals

@SpringBootTest
class RedisTest {
    @Autowired
    lateinit var redisTemplate: RedisTemplate<Any, Any>

    @Autowired
    @Qualifier("redisTemplateForMenuItem")
    lateinit var menuItemRedisTemplate: RedisTemplate<String, MenuItem>

    @Test
    fun contextLoads() {
        redisTemplate.opsForValue().set("test", 5)
        val value = redisTemplate.opsForValue().get("test")
        println("Value from Redis: $value")
        assertEquals(5, value)
    }

    @Test
    fun testJsonSerialization() {
        val menuItem = MenuItem(
            name = "Test Item",
            size = Size.大杯,
            price = Money.of(CurrencyUnit.of("CNY"), 25.0)
        )
        menuItemRedisTemplate.opsForValue().set("menuItem:1", menuItem)
        val retrievedItem = menuItemRedisTemplate.opsForValue().get("menuItem:1")
        println("Retrieved MenuItem: $retrievedItem")
        assertEquals(menuItem, retrievedItem)
    }
}