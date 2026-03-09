package sky.learnspringbinarytea.runner

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.repository.MenuRepository
import java.time.Duration

@Component
@Order(1)
class MenuCacheRunner(
    val redisTemplate: RedisTemplate<String, MenuItem>,
    val menuRepository: MenuRepository
): ApplicationRunner {
    val logger = LoggerFactory.getLogger(javaClass)
    override fun run(args: ApplicationArguments) {
        val items= menuRepository.queryAllItems()
        redisTemplate.opsForList().leftPushAll("binarytea-menu", items)
        redisTemplate.expire("binarytea-menu", Duration.ofMinutes(5))
        logger.info("Load {} items into Redis cache", items.size)
    }
}
