package sky.learnspringbinarytea.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.money.Money
import java.io.Serializable
import java.time.LocalDateTime


enum class Size {
    小杯, 中杯, 大杯
}

@Entity
@Table(name = "t_menu")
data class MenuItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,// id 字段为可空类型，插入前为 null，插入成功后会被填充
    var name: String?,
    @Enumerated(EnumType.STRING)
    var size: Size?,
    @Convert(converter = MoneyConverter::class)
    var price: Money?,

    @Column(updatable = false)
    @CreationTimestamp
    var createTime: LocalDateTime? = null,// createTime 字段为可空类型，插入前为 null，插入成功后会被填充
    @UpdateTimestamp
    var updateTime: LocalDateTime? = null// updateTime 字段为可空类型，插入前为 null，插入成功后会被填充
) : Serializable {
    companion object {
        private  const val serialVersionUID = 8585684450527309518L
    }

}
