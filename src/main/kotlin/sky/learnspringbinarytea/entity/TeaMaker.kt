package sky.learnspringbinarytea.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "t_tea_maker")
data class TeaMaker(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String? ,
    @OneToMany(mappedBy = "maker")
    var orders:MutableList<Order>?=mutableListOf(),
    @Column(updatable = false)
    @CreationTimestamp
    var createTime: LocalDateTime?=null,
    @UpdateTimestamp
    var updateTime: LocalDateTime?=null


) : Serializable {
    // Avoid recursive toString caused by bidirectional maker.orders <-> order.maker
    override fun toString(): String = "TeaMaker(id=$id, name=$name, orders=${orders?.size}, createTime=$createTime, updateTime=$updateTime)"
}