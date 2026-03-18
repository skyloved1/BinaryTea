package sky.learnspringbinarytea.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.web.filter.ShallowEtagHeaderFilter
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
    ///本节中谈到的 ShallowEtagHeaderFilter 本质上并不会减少服务器端的计算工作 量，
    // 所有的操作都会发生，它对发送给客户端的数据进行了额外的计算，产生一个 ETag，
    // 如果这个值与客户端传过来的值一样，就不再传输内容，直接告诉客户端没有变化，
    // 用以前 的就行。因此，它是用额外的服务端 CPU 时间优化了网络传输和客户端感受到的耗时。
    @Bean
    fun global()= ShallowEtagHeaderFilter()
}
