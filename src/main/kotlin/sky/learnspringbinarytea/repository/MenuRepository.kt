package sky.learnspringbinarytea.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.model.MenuItem
import java.math.BigDecimal
import java.util.*

@Repository
class MenuRepository( var jdbcTemplate: JdbcTemplate) {

    fun countMenuItems(): Long{
        val sql="SELECT COUNT(*) FROM t_menu"
        return jdbcTemplate.queryForObject<Long>(sql) ?: 0L
    }

    fun queryForItemById(id: Long): MenuItem? {
        val sql = "SELECT * FROM t_menu WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper(), id)
    }
    fun queryAllItems(): List<MenuItem> {
        return jdbcTemplate.query("select * from t_menu", rowMapper())
    }
    private  fun rowMapper(): RowMapper<MenuItem> {
        return RowMapper{
            rs, rowNum ->
            MenuItem(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                price = BigDecimal.valueOf(rs.getLong("price")/100.0),
                createTime = Date(rs.getTimestamp("create_time").time),
                updateTime = Date(rs.getTimestamp("update_time").time),
                size = rs.getString("size")
            )
        }
        }

}