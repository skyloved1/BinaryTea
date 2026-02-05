package sky.learnspringbinarytea

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import sky.learnspringbinarytea.metric.SalesMetrics
import java.lang.StringBuilder
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import kotlin.random.Random

@SpringBootApplication
@RestController
open class LearnSpringBinaryTeaApplication(
    private val ctx: ApplicationContext
) {
    @GetMapping("/hello")
    open fun hello(): String {
        // 通过 ApplicationContext 获取 Bean（代理对象），确保 AOP 能生效
        val proxy = ctx.getBean<LearnSpringBinaryTeaApplication>()
        return "Hello," + proxy.helloWithArgs(StringBuilder("Binary Tea"))
    }

    open fun helloWithArgs(name: StringBuilder): String {
        return "$name!"
    }
}

@RestController("/scheduled-tasks")
class ScheduledTasks(
    val salesMetrics: SalesMetrics,
    val ex:ThreadPoolTaskScheduler
) {

    companion object {
        val random = Random(1)
        val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    var task: ScheduledFuture<*>? = null


    @GetMapping("/start")
    fun startTask(): String {
        task = ex.scheduleAtFixedRate(this::task, Duration.ofSeconds(3))
        return "Task Started"
    }
    @GetMapping("/stop")
    fun endTask(): String {
        task?.cancel(false);
        return "Task Stopped"

    }

    fun task() {
        val amount = random.nextInt(10, 100)
        salesMetrics.makeNewOrder(amount)
        logger.info("New order processed with amount: $amount")
    }
}


fun main(args: Array<String>) {
    runApplication<LearnSpringBinaryTeaApplication>(*args)
}
