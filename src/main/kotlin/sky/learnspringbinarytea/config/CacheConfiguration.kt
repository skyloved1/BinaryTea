package sky.learnspringbinarytea.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import sky.learnspringbinarytea.service.MenuService
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud
import sky.learnspringbinarytea.repository.OrderRepository
import sky.learnspringbinarytea.service.OrderService

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
    @Bean
    fun orderCacheBean(orderRepository: OrderRepository)= OrderService(
        orderRepository = orderRepository
    )
}
