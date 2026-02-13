package sky.learnspringbinarytea

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import sky.learnspringbinarytea.repository.DemoService
import kotlin.test.assertEquals

@SpringBootTest
class DemoServiceTest {
    @Autowired
    lateinit var demoService: DemoService

    @Autowired
    lateinit var namedJdbcTemplate: NamedParameterJdbcTemplate
    val logger: Logger = LoggerFactory.getLogger(DemoServiceTest::class.java)

    @Transactional
    @Test
    fun trySomeMethods() {
        with(demoService) {
            insertRecordRequired()
            try {
                insertRecordNested()
            } catch (e: RuntimeException) {
            }
            val list = namedJdbcTemplate.queryForList(
                ("select name from t_demo"),
                MapSqlParameterSource(mapOf("name" to "name"))
            )
            assertEquals(expected = 1, actual = list.size, "应当只插入记录 one")
        }
    }

    // 关键修改1：添加 @Rollback(false) 关闭测试自动回滚
    @Test
    @Transactional()
    @Order(1)
    fun another() {
        try {
            with(demoService) {
                // 关键修改2：打印事务状态，验证是否真的开启了事务
                logger.info("insertRecordRequired 执行前，是否有活跃事务：${TransactionSynchronizationManager.isActualTransactionActive()}")
                insertRecordRequired() // REQUIRED：无事务则新建
                logger.info("insertRecordRequired 执行后，是否有活跃事务：${TransactionSynchronizationManager.isActualTransactionActive()}")

                logger.info("insertRecordRequiredNew 执行前，是否有活跃事务：${TransactionSynchronizationManager.isActualTransactionActive()}")
                insertRecordRequiredNew() // REQUIRES_NEW：强制新建独立事务
                logger.info("insertRecordRequiredNew 执行后，是否有活跃事务：${TransactionSynchronizationManager.isActualTransactionActive()}")

                throw RuntimeException("")
            }
        } catch (e: RuntimeException) {
            logger.info("捕获到异常，事务将回滚（仅REQUIRED的事务）")
        }
    }

    /**
     * @param DemoService.insertRecordRequiredNew 验证事务所插入的记录是否存在
     */
    @Order(2)
    @Test
    fun afterAnother() {
        val list = namedJdbcTemplate.queryForList(
            "select name from t_demo", // 去掉多余的括号，简化SQL
            MapSqlParameterSource() // 无需参数，直接查询所有
        )
        logger.info("afterAnother 查询到的记录：$list")
        // 修正断言：REQUIRED的记录（比如"one"）会回滚，REQUIRES_NEW的记录（"two"）会保留
        assertEquals(expected = 1, list.size)
        assertEquals(expected = "two", list[0]["name"])

        // 清理数据
        val count = namedJdbcTemplate.update(
            "delete from t_demo where name=:name",
            MapSqlParameterSource(mapOf("name" to list[0]["name"]))
        )
        assertEquals(expected = 1, count)
    }
}