package sky.learnspringbinarytea.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.annotation.Order

@Aspect
@Configuration
@EnableAspectJAutoProxy
open class AopConfiguration {
    val logger = LoggerFactory.getLogger(AopConfiguration::class.java)

    @Pointcut("execution(* sky.learnspringbinarytea.LearnSpringBinaryTeaApplication.hello())")
    fun methodType() {
    }

    @Before("methodType()")
    fun beforeHello() {
        logger.info("AOP Before Method: hello() is called")
    }

    @Pointcut("execution(* sky.learnspringbinarytea.LearnSpringBinaryTeaApplication.helloWithArgs(StringBuilder))")
    fun methodTypeWithArgs() {
    }

    @Before("methodTypeWithArgs()&& args(name)")
    fun beforeHelloWithArgs(name: StringBuilder) {
        logger.info("AOP Before Method: helloWithArgs() is called with argument: $name")
        logger.info("Modifying argument inside AOP before advice")
        name.append(" - modified by AOP")
    }

    @Around("methodTypeWithArgs()&& args(name)")
    fun aroundHelloWithArgs(pjp: ProceedingJoinPoint, name: StringBuilder): Any? {
        logger.info("AOP Around Method: helloWithArgs() is called with argument: $name")
        val modifiedName = StringBuilder("$name - modified by AOP Around")
        val result = pjp.proceed(arrayOf(modifiedName))
        logger.info("AOP Around Method: helloWithArgs() returned: $result")
        return result
    }
}