package sky.learnspringbinarytea.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import kotlin.Long

@Converter
class MoneyConverter : AttributeConverter<Money, Long> {
    override fun convertToDatabaseColumn(p0: Money?): Long? {
        return p0?.amountMajorLong?.times(100)
    }

    override fun convertToEntityAttribute(p0: Long?): Money? {
        return Money.of(
            CurrencyUnit.of("CNY"),
            BigDecimal.valueOf(p0?.div(100.0) ?: Long.MIN_VALUE.toDouble())
        )
    }

}

