package sky.learnspringbinarytea.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

enum class OrderStatus {
    ORDERED, PAID, MAKING, FINISHED, TAKEN
}

@Entity
@Table(name = "t_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    var maker: TeaMaker?,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "t_order_item",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    @OrderBy
    var items: MutableList<MenuItem>,

    @Embedded
    var amount: Amount?,
    @Enumerated
    var status: OrderStatus = OrderStatus.ORDERED,
    @CreationTimestamp
    @Column(updatable = false)
    var createTime: LocalDateTime? = null,
    @CreationTimestamp
    var updateTime: LocalDateTime? = null
) : Serializable {
    // Keep toString compact and non-recursive to prevent StackOverflowError when logging
    override fun toString(): String = "Order(id=$id, makerId=${maker?.id}, status=$status, items=${items.size}, amount=$amount)"
}