package sky.learnspringbinarytea.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.datatype.jodamoney.JodaMoneyModule

@Configuration
class JsonMapperConfiguration {
    @Bean
    fun customerzier()= JsonMapperBuilderCustomizer{
        it.addModule(JodaMoneyModule())
    }
}
