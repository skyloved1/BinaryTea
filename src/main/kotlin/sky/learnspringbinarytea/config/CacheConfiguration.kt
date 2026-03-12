package sky.learnspringbinarytea.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sky.learnspringbinarytea.cache.MenuService
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager = ConcurrentMapCacheManager("menu")

    @Bean
    fun menuCacheBean(menuRepositoryByCrud: MenuRepositoryByCrud) = MenuService(
        menuRepositoryByCrud = menuRepositoryByCrud
    )
}
