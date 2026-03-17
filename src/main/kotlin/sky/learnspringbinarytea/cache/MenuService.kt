package sky.learnspringbinarytea.cache

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.NewMenuItemForm
import sky.learnspringbinarytea.entity.Size
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud

@Service
@CacheConfig(cacheNames = ["menu"])
class MenuService(
    private val menuRepositoryByCrud: MenuRepositoryByCrud
) {


    @Cacheable
    fun getAllMenu(): List<MenuItem> = menuRepositoryByCrud.findAll().toList()

    @Cacheable(key = "#root.methodName+ '-' + #name + '-' + #size")
    fun getByNameAndSize(name: String, size: Size) =
        menuRepositoryByCrud.getMenuItemByNameAndSize(name, size).firstOrNull()

    @Cacheable(key = "#root.methodName + '-' + #name")
    fun getByName(name: String) = menuRepositoryByCrud.getByName(name)

    @Cacheable(key = "#root.methodName + '-' + #id")
    fun getById(id: Long) = menuRepositoryByCrud.findById(id).orElse(null)
    fun save(form: NewMenuItemForm) = menuRepositoryByCrud.saveAndFlush(
        MenuItem(
            name = form.name,
            price = form.price,
            size = form.size
        )
    )
    fun saveAll(items: List<MenuItem>): List<MenuItem> = menuRepositoryByCrud.saveAllAndFlush(items)
}
