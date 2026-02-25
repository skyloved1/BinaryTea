package sky.learnspringbinarytea.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sky.learnspringbinarytea.entity.OrderStatus
import kotlin.test.assertTrue

@SpringBootTest
class OrderRepositoryTest(
    @Autowired
    val orderRepository: OrderRepository
) {


    @Test
    @Transactional
    fun testFindByStatusOrderById(){
        assertTrue(actual = orderRepository.findByStatusOrderById(OrderStatus.FINISHED).isEmpty(), "没有订单的状态是完成的")
    }
    @Test
    @Transactional
    fun `testFindByMaker_NameContainingOrderByUpdateTimeDescId`() {
        val list = orderRepository.findByMaker_NameLikeIgnoreCaseOrderByUpdateTimeDescId("李%")
        assertTrue(actual = list.isNotEmpty(), "应当有订单的制作者名字包含李")
        list.forEach { println("制作者名字：${it.maker?.name?:"""null"""},updateTime=${it.updateTime},id=${it.id}") }
    }
}