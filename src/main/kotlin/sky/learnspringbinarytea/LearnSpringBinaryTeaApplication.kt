package sky.learnspringbinarytea

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import sky.learnspringbinarytea.metric.SalesMetrics
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import kotlin.random.Random

@SpringBootApplication
@RestController
class LearnSpringBinaryTeaApplication {
    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, Binary Tea!"
    }
}

@RestController("/scheduled-tasks")
class ScheduledTasks(
    val salesMetrics: SalesMetrics,
) {

    companion object {
        val random = Random(1)
        val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    }

    var task: ScheduledFuture<*>? = null
    val scheduledExecutor: ThreadPoolTaskScheduler = ThreadPoolTaskScheduler().apply {
        this.poolSize = 1
        this.initialize()
    }

    @GetMapping("/start")
    fun startTask(): String {
        task = scheduledExecutor.scheduleAtFixedRate(this::task, Duration.ofSeconds(3))
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
