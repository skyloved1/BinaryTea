package sky.learnspringbinarytea.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Service
class DemoService(
    val namedJdbcTemplate: NamedParameterJdbcTemplate,
    val transactionTemplate: TransactionTemplate
) {

    val INSERT_SQL = """
        insert into t_demo (name,create_time,update_time) values (:name,now(),now())
    """.trimIndent()

    fun insertName(name: String): Int {
        val params = MapSqlParameterSource(mapOf("name" to name))
        return namedJdbcTemplate.update(INSERT_SQL, params)
    }

    @Transactional(readOnly = true)
    fun showNames(): String {
        return namedJdbcTemplate.queryForList(
            "select name from t_demo",
            MapSqlParameterSource(mapOf("name" to "name"))
        ).joinToString { it["name"].toString() }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun insertRecordRequired(): Int {
        return namedJdbcTemplate.update(INSERT_SQL, MapSqlParameterSource(mapOf("name" to "one")))
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun insertRecordRequiredNew(): Int {
        return namedJdbcTemplate.update(INSERT_SQL, MapSqlParameterSource(mapOf("name" to "two")))
    }

    @Transactional(propagation = Propagation.NESTED)
    fun insertRecordNested() {
        namedJdbcTemplate.update(INSERT_SQL, MapSqlParameterSource(mapOf("name" to "three")))
        throw RuntimeException("Nested transaction failed")
    }
    // 使用编程式事务管理
    fun showNamesProgrammatically() = transactionTemplate.apply {

        this.propagationBehavior = Propagation.REQUIRED.value()

        execute { status ->
            namedJdbcTemplate.queryForList("select name from t_demo;", MapSqlParameterSource(mapOf("name" to "name")))
                .joinToString { it["name"].toString() }
        }
    }
}
