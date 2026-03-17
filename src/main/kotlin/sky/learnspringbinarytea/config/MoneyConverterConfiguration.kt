package sky.learnspringbinarytea.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.datatype.jodamoney.JodaMoneyModule

@Configuration
class MoneyConverterConfiguration {

    @Bean
    fun toJson()= JodaMoneyModule()
}
