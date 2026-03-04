package sky.learnspringbinarytea.config

import io.lettuce.core.ReadFrom
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfiguration {

    @Bean
    fun lettuceCustomizer() = LettuceClientConfigurationBuilderCustomizer {
        configurationBuilder ->
        configurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED)
    }

}
