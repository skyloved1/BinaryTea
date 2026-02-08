package sky.learnspringbinarytea.runner

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import sky.learnspringbinarytea.repository.MenuRepository
@Component
class MenuPrinterRunner(val menuRepository: MenuRepository) : ApplicationRunner {
    val logger: Logger = LoggerFactory.getLogger(MenuPrinterRunner::class.java)
    override fun run(args: ApplicationArguments) {
        logger.info("共有{}个饮品可以选择。", menuRepository.countMenuItems())
        menuRepository.queryAllItems().forEach {
             logger.info("饮品：{}，大小：{}，价格：{}", it.name, it.size, it.price)
        }
    }
}


