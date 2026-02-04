package sky.learnspringbinarytea.metric

import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricBean {
    @Bean
    fun customMeterRegistry()= CompositeMeterRegistry().apply {
        add(SimpleMeterRegistry())
        add(LoggingMeterRegistry())
    }
}
