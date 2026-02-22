package sky.learnspringbinarytea.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

enum class OrderStatus{
    ORDERED,PAID,MAKING,FINISHED,TAKEN
}

@Entity
@Table(name = "t_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    var maker: TeaMaker? = null,
    @ManyToMany
    @JoinTable(name = "t_order_item", joinColumns = [JoinColumn(name ="item_id")],
        inverseJoinColumns = [JoinColumn(name ="order_id")])
    @OrderBy()
    var items: MutableList<TeaMaker>? = null,

    @Embedded
    var amount: Amount?= null,
    @Enumerated
    var status: OrderStatus= OrderStatus.ORDERED,
    @CreationTimestamp
    @Column(updatable = false)
    var createTime: LocalDateTime?,
    @CreationTimestamp
    var updateTime: LocalDateTime?
) {

}