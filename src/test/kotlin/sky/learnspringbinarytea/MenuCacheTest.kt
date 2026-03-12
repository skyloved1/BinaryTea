package sky.learnspringbinarytea

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sky.learnspringbinarytea.cache.MenuService
import sky.learnspringbinarytea.entity.Size
import kotlin.test.assertEquals

@SpringBootTest
class MenuCacheTest {
    @Autowired
    lateinit var menuService: MenuService


    @Test
    @Order(1)
    fun cacheMenu() {
        val menu1 = menuService.getAllMenu()
        val item1 = menuService.getByNameAndSize("Go橙汁", Size.中杯)!!

        menuService.getAllMenu().also {
            assertEquals(menu1, it, "Expected cached menu list, but got a different instance.")
        }
        menuService.getByNameAndSize("Go橙汁", Size.中杯).also {
            assertEquals(item1, it, "Expected cached menu item, but got a different instance.")
        }
    }

    @Test
    @Order(2)
    fun getCachedMenu() {

    }
}
