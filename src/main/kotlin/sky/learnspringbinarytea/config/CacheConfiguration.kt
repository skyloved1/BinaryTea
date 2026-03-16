package sky.learnspringbinarytea.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import sky.learnspringbinarytea.cache.MenuService
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(redisConnectionFactory).build()
    }

    @Bean
    fun menuCacheBean(menuRepositoryByCrud: MenuRepositoryByCrud) = MenuService(
        menuRepositoryByCrud = menuRepositoryByCrud
    )
}
