package sky.learnspringbinarytea.mybaits.typehandler

import org.apache.ibatis.type.EnumOrdinalTypeHandler
import org.apache.ibatis.type.EnumTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.TypeHandler
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import sky.learnspringbinarytea.entity.OrderStatus
import sky.learnspringbinarytea.entity.Size
import java.math.BigDecimal
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class SizeEnumTypeHandler : EnumTypeHandler<Size>(Size::class.java)
class OrderStatusEnumTypeHandler : EnumOrdinalTypeHandler<OrderStatus>(OrderStatus::class.java)
class MoneyTypeHandler: TypeHandler<Money> {
    override fun setParameter(
        ps: PreparedStatement?,
        i: Int,
        parameter: Money?,
        jdbcType: JdbcType?
    ) {
        ps?.setLong(i, parameter?.amountMinorLong ?: Long.MIN_VALUE)
    }

    override fun getResult(rs: ResultSet?, columnName: String?): Money? {
        return Money.of(
            CurrencyUnit.of("CNY"),
            BigDecimal.valueOf(rs?.getLong(columnName)?.div(100.0) ?: Long.MIN_VALUE.toDouble())
        )
    }

    override fun getResult(rs: ResultSet?, columnIndex: Int): Money? {
        return Money.of(
            CurrencyUnit.of("CNY"),
            BigDecimal.valueOf(rs?.getLong(columnIndex)?.div(100.0) ?: Long.MIN_VALUE.toDouble())
        )
    }

    override fun getResult(cs: CallableStatement?, columnIndex: Int): Money {
        return parseMoney(cs?.getLong(columnIndex))
    }
    private fun parseMoney(value: Long?): Money {
        return Money.ofMinor(CurrencyUnit.of("CNY"), value ?: Long.MIN_VALUE)
    }
}