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


) {
}