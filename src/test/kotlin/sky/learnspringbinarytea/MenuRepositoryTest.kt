package sky.learnspringbinarytea

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size
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
        assertItem(item, 1L, "Java咖啡", Size.中杯, Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(10.0)));
    }

    @Test
    @Transactional
    @Order(1)
    fun testInsertItem() {
        val item: MenuItem = MenuItem(
            name = "Go橙汁",
            size = Size.中杯,
            price = Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0)),
            id = null,
            createTime = null,
            updateTime = null,
        )
        val affected = menuRepository.insertItem(item)
        assertEquals(1, affected)
        assertNull(item.id)

        val queryItem: MenuItem? = menuRepository.queryForItemById(3L)
        queryItem?.let {
            assertItem(
                it,
                3L,
                "Go橙汁",
                Size.中杯,
                Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0))
            )
        }

        assertEquals(1, menuRepository.insertItemAndFillId(item))
        val itemWithModifiedId = menuRepository.queryForItemById(item.id!!)!!
        assertItem(
            itemWithModifiedId,
            4L,
            "Go橙汁",
            Size.中杯,
            Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0))
        )
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
    @Transactional
    fun testInsertItems() {
        val items = listOf("Go橙汁2", "Python柠檬茶", "JavaScript奶茶").map { name ->
            MenuItem(
                name = name,
                id = null,
                price = Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0)),
                size = Size.valueOf(listOf("小杯", "中杯", "大杯").random()),
                createTime = null,
                updateTime = null
            )
        }
        val affected = menuRepository.intsertItems(items)
        assertItem(
            menuRepository.queryForItemByName("Go橙汁2")!!,
            3L,
            "Go橙汁2",
            Size.valueOf(items[0].size.toString()),
            Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0))
        )
        assertItem(
            menuRepository.queryForItemByName("Python柠檬茶")!!,
            4L,
            "Python柠檬茶",
            Size.valueOf(items[1].size.toString()),
            Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0))
        )
        assertItem(
            menuRepository.queryForItemByName("JavaScript奶茶")!!,
            5L,
            "JavaScript奶茶",
            Size.valueOf(items[2].size.toString()),
            Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(12.0))
        )
        assertEquals(3, affected)
        menuRepository.deleteItem(3)
        menuRepository.deleteItem(4)
        menuRepository.deleteItem(5)
       // resetAutoIncrement(to = 2)
    }

    private fun assertItem(item: MenuItem, id: Long, name: String, size: Size, price: Money) {
        assertNotNull(item)
        assertEquals(id, item.id)
        assertEquals(name, item.name)
        assertEquals(price, item.price)
        assertEquals(size, item.size)
    }

    // kotlin
    fun resetAutoIncrement(to: Int) {
        try {
            ds.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    val next = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM `t_menu`").use { rs ->
                        if (rs.next()) rs.getLong(1) else 1L
                    }
                    val newVal = maxOf(to.toLong(), next) // 不会把 AUTO_INCREMENT 设置为比当前最大 id 小的值
                    stmt.executeUpdate("ALTER TABLE `t_menu` AUTO_INCREMENT = $newVal;")
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}