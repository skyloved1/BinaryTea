package sky.learnspringbinarytea.Config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class ScheduledThreadPoolConfiguration {
    @Bean
    fun threadPoolTaskScheduler(): ThreadPoolTaskScheduler =
        ThreadPoolTaskScheduler().apply {
            poolSize = 1
            // Name prefix is what shows up in logs; thread group is not printed by default
            setThreadNamePrefix("#custom# scheduled-task-")
            setThreadGroupName("#custom# scheduled-task-pool")
            initialize()
        }
}