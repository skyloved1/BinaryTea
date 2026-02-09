package sky.learnspringbinarytea

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sky.learnspringbinarytea.model.MenuItem
import sky.learnspringbinarytea.repository.MenuRepository
import java.math.BigDecimal
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MenuRepositoryTest {

    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var ds: DataSource

    @Test
    fun testCount() {
        val count = menuRepository.countMenuItems()
        assertEquals(2, count)
    }

    @Test
    fun testQueryAllItems() {
        val items = menuRepository.queryAllItems()
        assertNotNull(items)
        assertFalse(items.isEmpty())
        assertEquals(2, items.size)

    }

    @Test
    fun testQueryForItemById() {
        val item = menuRepository.queryForItemById(1)
        assertNotNull(item)
        assertEquals(1, item.id)
        assertItem(item, 1L, "Java咖啡", "中杯", BigDecimal.valueOf(10.00));
    }

    @Test
    @Order(1)
    fun testInsertItem() {
        val item: MenuItem = MenuItem(
            name = "Go橙汁",
            size = "中杯",
            price = BigDecimal.valueOf(12.0),
            id = null,
            createTime = null,
            updateTime = null,
        )
        val affected = menuRepository.insertItem(item)
        assertEquals(1, affected)
        assertNull(item.id)

        val queryItem: MenuItem? = menuRepository.queryForItemById(3L)
        queryItem?.let { assertItem(it, 3L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0)) }

        assertEquals(1, menuRepository.insertItemAndFillId(item))
        val itemWithModifiedId = menuRepository.queryForItemById(item.id!!)!!
        assertItem(itemWithModifiedId, 4L, "Go橙汁", "中杯", BigDecimal.valueOf(12.0))
    }

    @Test
    @Order(2)
    fun testDeleteItem() {
        val affected = menuRepository.deleteItem(id = 3L)//删除 item
        val affected2 = menuRepository.deleteItem(id = 4L)//删除 itemWithModifiedId
        assertEquals(1, affected)
        assertEquals(1, affected2)
        assertNull(menuRepository.queryForItemById(3L))
        assertNull(menuRepository.queryForItemById(4L))
        resetAutoIncrement(2)
    }

    @Test
    @Order(3)
    fun testInsertItems() {
        val items = listOf("Go橙汁2", "Python柠檬茶", "JavaScript奶茶").map {
            name->
            MenuItem(
                name = name,
                id = null,
                price = BigDecimal.valueOf(12.0),
                size = listOf("小杯", "中杯", "大杯").random(),
                createTime = null,
                updateTime = null
            )
        }
        val affected = menuRepository.intsertItems(items)
        assertItem(menuRepository.queryForItemById(3)!!, 3L, "Go橙汁2", items[0].size, BigDecimal.valueOf(12.0))
        assertItem(menuRepository.queryForItemById(4)!!, 4L, "Python柠檬茶", items[1].size, BigDecimal.valueOf(12.0))
        assertItem(menuRepository.queryForItemById(5)!!, 5L, "JavaScript奶茶", items[2].size, BigDecimal.valueOf(12.0))
        assertEquals(3, affected)
        menuRepository.deleteItem(3)
        menuRepository.deleteItem(4)
        menuRepository.deleteItem(5)
        resetAutoIncrement(to = 2)
    }

    private fun assertItem(item: MenuItem, id: Long, name: String, size: String, price: BigDecimal) {
        assertNotNull(item)
        assertEquals(id, item.id)
        assertEquals(name, item.name)
        assertEquals(price, item.price)
        assertEquals(size, item.size)
    }
    fun resetAutoIncrement(to: Int ) {
        //重置自增主键
        ds.connection.prepareStatement("ALTER TABLE `t_menu` AUTO_INCREMENT = ${to};")
            .executeUpdate()
    }
}