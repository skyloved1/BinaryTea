package sky.learnspringbinarytea.repository

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size
import java.math.BigDecimal
import java.sql.PreparedStatement

@Repository
class MenuRepository(
    val jdbcTemplate: JdbcTemplate,
    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {

    fun countMenuItems(): Long {
        val sql = "SELECT COUNT(*) FROM t_menu"
        return jdbcTemplate.queryForObject<Long>(sql) ?: 0L
    }

    fun queryForItemById(id: Long): MenuItem? {
        val sql = "SELECT * FROM t_menu WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper(), id).firstOrNull()
    }
    fun queryForItemByName(name: String): MenuItem? {
        val sql = "SELECT * FROM t_menu WHERE name = ?"
        return jdbcTemplate.query(sql, rowMapper(), name).firstOrNull()
    }

    fun queryAllItems(): List<MenuItem> {
        return jdbcTemplate.query("select * from t_menu", rowMapper())
    }

    private fun rowMapper(): RowMapper<MenuItem> {
        return RowMapper { rs, rowNum ->
            MenuItem(
                name = rs.getString("name"),
                price = Money.of(CurrencyUnit.of("CNY"), BigDecimal.valueOf(rs.getLong("price") / 100.0)),
                size = Size.valueOf(rs.getString("size")),
                id = rs.getLong("id"),
                createTime = rs.getTimestamp("create_time").toLocalDateTime(),
                updateTime = rs.getTimestamp("update_time").toLocalDateTime()
            )
        }
    }


    val sql = "INSERT INTO t_menu (name, price, size, create_time, update_time) VALUES (?, ?, ?, now(), now())"
    val insertSqlNamed =
        "INSERT INTO t_menu (name, price, size, create_time, update_time) VALUES (:name, :price, :size, now(), now())"

    fun insertItem(item: MenuItem): Int {
        val namedParameterSql = MapSqlParameterSource(
            mapOf(
                "name" to item.name,
                "price" to item.price!!.amountMinorLong,
                "size" to item.size.toString()
            )
        )
        return namedParameterJdbcTemplate.update(insertSqlNamed, namedParameterSql)
    }

    fun intsertItems(items: List<MenuItem>): Int {
        val countArr = jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val item = items[i]
                ps.setString(1, item.name)
                item.price?.let { ps.setLong(2, it.amountMinorLong) }
                ps.setString(3, item.size.toString())
            }

            override fun getBatchSize(): Int {
                return items.size
            }
        })
        return countArr.sum()
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
                    setLong(2, item.price!!.amountMinorLong)
                    setString(3, item.size.toString())
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

    fun deleteItemByName(name: String): Int {
        val sql = "DELETE FROM t_menu WHERE name = ?"
        return jdbcTemplate.update(sql, name)
    }
}
