package sky.learnspringbinarytea.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.model.MenuItem
import java.math.BigDecimal
import java.util.*
import kotlin.text.toLong

@Repository
class MenuRepository(var jdbcTemplate: JdbcTemplate) {

    fun countMenuItems(): Long {
        val sql = "SELECT COUNT(*) FROM t_menu"
        return jdbcTemplate.queryForObject<Long>(sql) ?: 0L
    }

    fun queryForItemById(id: Long): MenuItem? {
        val sql = "SELECT * FROM t_menu WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper(), id).firstOrNull()
    }

    fun queryAllItems(): List<MenuItem> {
        return jdbcTemplate.query("select * from t_menu", rowMapper())
    }

    private fun rowMapper(): RowMapper<MenuItem> {
        return RowMapper { rs, rowNum ->
            MenuItem(
                name = rs.getString("name"),
                price = BigDecimal.valueOf(rs.getLong("price") / 100.0),
                size = rs.getString("size"),
                id = rs.getLong("id"),
                createTime = Date(rs.getTimestamp("create_time").time),
                updateTime = Date(rs.getTimestamp("update_time").time)
            )
        }
    }


    val sql = "INSERT INTO t_menu (name, price, size, create_time, update_time) VALUES (?, ?, ?, now(), now())"

    fun insertItem(item: MenuItem): Int {
        return jdbcTemplate.update(
            sql,
            item.name,
            item.price.multiply(BigDecimal.valueOf(100)).toLong(),
            item.size,
        )
    }

    /**
     * @param item 需要插入的 MenuItem 对象，插入成功后会自动填充 id 字段
     * @return 受影响的行数，通常为 1 表示插入成功，0 表示插入失败
     */
    fun insertItemAndFillId(item: MenuItem): Int {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        val affected: Int = jdbcTemplate.update(
            { connection ->
                connection.prepareStatement(sql, arrayOf("id")).apply {
                    setString(1, item.name)
                    setLong(2, item.price.multiply(BigDecimal.valueOf(100)).toLong())
                    setString(3, item.size)
                }
            }, keyHolder
        )
        if (affected == 1) {
            item.id = keyHolder.key?.toLong()
        }
        return affected
    }

    fun deleteItem(id: Long): Int {
        val sql = "DELETE FROM t_menu WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}