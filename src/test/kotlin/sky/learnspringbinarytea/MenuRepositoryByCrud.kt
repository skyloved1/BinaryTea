package sky.learnspringbinarytea

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class MenuRepositoryTestByCrud {
    @Autowired
    lateinit var menuRepository: MenuRepositoryByCrud

    @Test
    fun testCount() {
        val count = menuRepository.count()
        assertEquals(2, count, "初始数据表中应当有两条记录")
    }

    @Test
    fun testGetAll() {
        val items = menuRepository.findAll().also { it.forEach { menuItem -> println(menuItem) } }
        assertEquals(2, items.count(), "初始数据表中应当有两条记录")
    }

    @Test
    fun testUpdate() {
        val menuItem = MenuItem(
            name = "绿茶",
            size = Size.大杯,
            price = Money.of(CurrencyUnit.of("CNY"), 20.0)
        )

        // 在事务内保存并立即 flush（saveAndFlush 本身也是 @Transactional，但在测试中属于同一事务）
        val savedItem = menuRepository.saveAndFlush(menuItem)
        println("保存后的菜单项: $savedItem")

        assertEquals(menuItem.name, savedItem.name)
        assertEquals(menuItem.size, savedItem.size)
        assertEquals(menuItem.price, savedItem.price)

        // 删除应使用保存后的实体
        menuRepository.delete(savedItem)
        assertFalse(menuRepository.findById(savedItem.id!!).isPresent, "删除后的菜单项应当无法通过 ID 查找到")
    }
}
