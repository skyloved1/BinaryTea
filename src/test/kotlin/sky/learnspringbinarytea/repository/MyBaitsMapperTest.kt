package sky.learnspringbinarytea.repository

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import sky.learnspringbinarytea.entity.Amount
import sky.learnspringbinarytea.entity.Order
import sky.learnspringbinarytea.entity.TeaMaker
import sky.learnspringbinarytea.mybaits.MenuItemMapper
import sky.learnspringbinarytea.mybaits.OrderMapper
import sky.learnspringbinarytea.mybaits.TeaMakerMapper
import kotlin.collections.emptyList
import kotlin.test.assertEquals

@SpringBootTest
class MyBaitsMapperTest(

) {
    @Autowired
    lateinit var orderMapper: OrderMapper
    @Autowired
    lateinit var teaMakerMapper: TeaMakerMapper
    @Autowired
    lateinit var  menuItemMapper: MenuItemMapper

    @Test
    @Transactional
    @Rollback
    fun testSaveAndFind(){
        val maker: TeaMaker?=teaMakerMapper.findById(4L);
        assertNotNull(maker)
        val order= Order(
            maker = maker,
            items = mutableListOf(),
            amount = Amount(
                discount = 90,
                totalAmount = Money.ofMinor(CurrencyUnit.of("CNY"), 1200),
                payAmount = Money.ofMinor(CurrencyUnit.of("CNY"), 1080)
            )
        )
        assertEquals(expected = 1,orderMapper.save(order))
        val orderId: Long?=order.id
        assertNotNull(orderId)
        assertEquals(expected = 1,orderMapper.addOrderItem(orderId, menuItemMapper.findById(2L)!!))

        val savedOrder=orderMapper.findById(orderId)
        assertNotNull(savedOrder)
        assertEquals(expected = 90, savedOrder.amount?.discount)
        println(savedOrder)
    }
}
