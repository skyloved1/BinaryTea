package sky.learnspringbinarytea

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class LearnSpringBinaryTeaApplication{
    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, Binary Tea!"
    }
}

fun main(args: Array<String>) {
    runApplication<LearnSpringBinaryTeaApplication>(*args)
}
