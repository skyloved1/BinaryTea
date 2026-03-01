package sky.learnspringbinarytea.runner

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import sky.learnspringbinarytea.entity.*
import sky.learnspringbinarytea.repository.MenuRepositoryByCrud
import sky.learnspringbinarytea.repository.OrderRepository
import sky.learnspringbinarytea.repository.TeaMakerRepository



open class DataInitRunner(
    val menuRepository: MenuRepositoryByCrud,
    val orderRepository: OrderRepository,
    val teaMakerRepository: TeaMakerRepository,
) : ApplicationRunner {
    val logger = LoggerFactory.getLogger(DataInitRunner::class.java)

    @Transactional
    override fun run(args: ApplicationArguments) {
        val randomSize = listOf("大杯", "中杯", "小杯")
        val menuItems = listOf("Go橙汁", "Python气泡水", "JavaScript苏打水")
            .map { name ->
                MenuItem(
                    name = name,
                    size = Size.valueOf(randomSize.random()),
                    price = Money.of(CurrencyUnit.of("CNY"), 12.0)
                )
            }.map { item ->
                menuRepository.saveAndFlush(item)
            }.also { items -> items.forEach { item -> logger.info("item:{}", item) } }

        val makerList = listOf("李磊", "韩梅梅", "王大锤")
            .map { name ->
                TeaMaker(
                    name = name
                )
            }
            .map { teaMaker ->
                teaMakerRepository.saveAndFlush(teaMaker)
            }
            .also { teaMakers -> teaMakers.forEach { teaMaker -> logger.info("teaMaker:{}", teaMaker) } }
        val orderList: List<Order> = makerList.mapIndexed { index, teaMaker ->
            Order(
                maker = teaMaker,
                items = mutableListOf(menuItems[index % menuItems.size]),
                amount = Amount(
                    discount = 90,
                    totalAmount = Money.ofMinor(CurrencyUnit.of("CNY"), 1200),
                    payAmount = Money.ofMinor(CurrencyUnit.of("CNY"), 1080)
                ),
                status = OrderStatus.ORDERED
            )
        }.map { order -> orderRepository.saveAndFlush(order) }

        orderList.forEach { order -> logger.info("order:{}", order) }

    }
}
