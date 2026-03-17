package sky.learnspringbinarytea.serializer

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind.ser.std.StdSerializer
import tools.jackson.datatype.jodamoney.JodaMoneyModule
import com.fasterxml.jackson.*
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
// json 序列化和反序列化
//现在未启用由于 joda-money 模块已经提供了对 Money 类型的序列化和反序列化支持，因此我们不需要自定义 MoneySerializer 和 MoneyDeserializer 了。我们可以直接使用 JodaMoneyModule 来处理 Money 类型的 JSON 转换。
class MoneySerializer : StdSerializer<Money>(Money::class.java) {
    override fun serialize(
        value: Money?,
        gen: JsonGenerator?,
        provider: SerializationContext?
    ) {
        if (value == null || gen == null) {
            return
        }
        with(gen) {
            writeStartObject()
            writeNumber(value.amount)
        }
    }
}

class MoneyDeserializer : StdDeserializer<Money>(Money::class.java) {
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): Money? {
        return Money.of(CurrencyUnit.of("CNY"), p?.decimalValue ?: return null)
    }
}
@ReadingConverter
class MoneyReadder: Converter<String, Money> {
    override fun convert(source: String): Money {
        return Money.of(CurrencyUnit.of("CNY"),source.toBigDecimal())
    }
}

