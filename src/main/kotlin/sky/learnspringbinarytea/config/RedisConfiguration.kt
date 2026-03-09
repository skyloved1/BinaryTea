package sky.learnspringbinarytea.config

import io.lettuce.core.ReadFrom
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import sky.learnspringbinarytea.entity.MenuItem

@Configuration
class RedisConfiguration {

    @Bean
    fun lettuceCustomizer() = LettuceClientConfigurationBuilderCustomizer { configurationBuilder ->
        configurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED)
    }

    @Bean
    fun redisTemplateForMenuItem(connectionFactory: RedisConnectionFactory) = RedisTemplate<String, MenuItem>().apply {
        this.connectionFactory = connectionFactory
    }

}
