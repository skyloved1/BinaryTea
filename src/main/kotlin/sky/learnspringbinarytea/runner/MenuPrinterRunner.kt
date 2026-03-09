package sky.learnspringbinarytea.runner

import org.apache.logging.log4j.spi.Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.repository.MenuRepository

@Component
@Order(2)
class MenuPrinterRunner(
    val menuRepository: MenuRepository,
    val redisTemplate:RedisTemplate<String, MenuItem>
) : ApplicationRunner {
    val logger: Logger = LoggerFactory.getLogger(MenuPrinterRunner::class.java)
    override fun run(args: ApplicationArguments) {
        if (redisTemplate.hasKey("binarytea-menu")) {
            val cachedMenu = redisTemplate.opsForList().range("binarytea-menu", 0, -1)
            logger.info("从 Redis 缓存中获取菜单，共{}个饮品可以选择。", cachedMenu?.size ?: 0)
            cachedMenu.forEach {
                logger.info("饮品：{}", it)
            }
            return
        }
        logger.info("共有{}个饮品可以选择。", menuRepository.countMenuItems())
        menuRepository.queryAllItems().forEach {
            logger.info("饮品：{}，大小：{}，价格：{}", it.name, it.size, it.price)
        }
    }
}


