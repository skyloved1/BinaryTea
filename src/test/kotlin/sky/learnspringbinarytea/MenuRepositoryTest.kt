package sky.learnspringbinarytea

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sky.learnspringbinarytea.model.MenuItem
import sky.learnspringbinarytea.repository.MenuRepository
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest
class MenuRepositoryTest {

    @Autowired lateinit var menuRepository: MenuRepository
    @Test
    fun testCount(){
        val count=menuRepository.countMenuItems()
        assertEquals(2,count)
    }
    @Test
    fun testQueryAllItems(){
        val items=menuRepository.queryAllItems()
        assertNotNull(items)
        assertFalse(items.isEmpty())
        assertEquals(2,items.size)

    }
    @Test
    fun testQueryForItemById(){
        val item=menuRepository.queryForItemById(1)
        assertNotNull(item)
        assertEquals(1,item.id)
        assertItem(item, 1L, "Java咖啡", "中杯", BigDecimal.valueOf(10.00));
    }

    private fun assertItem(item: MenuItem, id: Long, name: String, size: String, price: BigDecimal,) {
        assertNotNull(item)
        assertEquals(id, item.id)
        assertEquals(name, item.name)
        assertEquals(price, item.price)
        assertEquals(size,item.size)
    }
}