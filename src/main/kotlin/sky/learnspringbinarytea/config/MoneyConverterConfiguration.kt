package sky.learnspringbinarytea.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sky.learnspringbinarytea.serializer.MoneyReadder
import tools.jackson.datatype.jodamoney.JodaMoneyModule

@Configuration
class MoneyConverterConfiguration {

    @Bean
    fun toJson()= JodaMoneyModule()
    @Bean
    fun stringToMoneyConverter() = MoneyReadder()
}
