package sky.learnspringbinarytea.cache

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud


@CacheConfig(cacheNames = ["menu"])
open class MenuService(
    private val menuRepositoryByCrud: MenuRepositoryByCrud
) {


    @Cacheable
    open fun getAllMenu(): List<MenuItem> = menuRepositoryByCrud.findAll().toList()

    @Cacheable(key = "#root.methodName+ '-' + #name + '-' + #size")
    open fun getByNameAndSize(name: String, size: Size) =
        menuRepositoryByCrud.getMenuItemByNameAndSize(name, size).firstOrNull()

}
