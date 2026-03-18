package sky.learnspringbinarytea.service

import org.joda.money.Money
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sky.learnspringbinarytea.entity.Amount
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Order
import sky.learnspringbinarytea.repository.OrderRepository
import java.math.RoundingMode

@Service
@Transactional
open class OrderService(
    private val orderRepository: OrderRepository
) {
    @Cacheable("orders")
    open fun getAllOrders()=orderRepository.findAll()

    @CacheEvict("orders", allEntries = true)
    open fun createOrder(itemList: List<MenuItem>,discount: Int): Order {
        val total=itemList.map {
            item ->
            item.price
        }.let { monies ->
            Money.total(monies)
        }
        val pay=total.multipliedBy(discount/ 100.0f.toDouble(), RoundingMode.HALF_DOWN)
        val amount= Amount(
            discount = discount,
            totalAmount = total,
            payAmount = pay
        )
        val order= Order(
            maker = null,
            items = itemList.toMutableList(),
            amount = amount
        )
        return orderRepository.saveAndFlush(order)
    }
}
