package sky.learnspringbinarytea.config

import io.lettuce.core.ReadFrom
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import sky.learnspringbinarytea.entity.MenuItem
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfiguration {



    @Bean
    fun redisTemplateForMenuItem(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ) =
        RedisTemplate<String, MenuItem>().apply {
            val serializer = JacksonJsonRedisSerializer(objectMapper, MenuItem::class.java)
            this.connectionFactory = connectionFactory
            this.keySerializer = RedisSerializer.string()
            this.valueSerializer = serializer
        }

    @Bean
    fun redisTemplateForJson(connectionFactory: RedisConnectionFactory) = RedisTemplate<String, Any>().apply {
        this.connectionFactory = connectionFactory
        this.keySerializer = RedisSerializer.string()
        this.valueSerializer = RedisSerializer.json()
        this.hashKeySerializer = RedisSerializer.string()
        this.hashValueSerializer = RedisSerializer.json()
    }

}
