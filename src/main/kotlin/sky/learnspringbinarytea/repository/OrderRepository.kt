package sky.learnspringbinarytea.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.entity.Order
import sky.learnspringbinarytea.entity.OrderStatus

@Repository
interface OrderRepository: JpaRepository<Order, Long> {
    fun findByStatusOrderById(status: OrderStatus): MutableList<Order>
    // Use ContainingIgnoreCase so Spring Data adds %...% automatically
    fun findByMaker_NameLikeIgnoreCaseOrderByUpdateTimeDescId(makerName: String): MutableList<Order>
}