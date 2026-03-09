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

@Configuration
class MoneySerializerConfig {
    val logger= org.slf4j.LoggerFactory.getLogger(this::class.java)
    @Bean
    fun moneySerializerModule()= JodaMoneyModule()
}