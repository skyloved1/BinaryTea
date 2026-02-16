package sky.learnspringbinarytea.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.Long.Companion.MIN_VALUE


enum class Size {
    小杯, 中杯, 大杯
}


@Converter
class MoneyConverter : AttributeConverter<Money, Long> {
    override fun convertToDatabaseColumn(p0: Money?): Long? {
        return p0?.amountMajorLong?.times(100)
    }

    override fun convertToEntityAttribute(p0: Long?): Money? {
        return Money.of(
            CurrencyUnit.of("CNY"),
            BigDecimal.valueOf(p0?.div(100.0) ?: MIN_VALUE.toDouble())
        )
    }

}


@Entity
class MenuItem(
    @Id
    @GeneratedValue
    var id: Long?,// id 字段为可空类型，插入前为 null，插入成功后会被填充
    var name: String?,
    @Enumerated(EnumType.STRING)
    var size: Size?,
    @Convert(converter = MoneyConverter::class)
    var price: Money?,

    @Column(updatable = false)
    @CreationTimestamp
    var createTime: LocalDateTime?,// createTime 字段为可空类型，插入前为 null，插入成功后会被填充
    @UpdateTimestamp
    var updateTime: LocalDateTime?// updateTime 字段为可空类型，插入前为 null，插入成功后会被填充


) {

}
