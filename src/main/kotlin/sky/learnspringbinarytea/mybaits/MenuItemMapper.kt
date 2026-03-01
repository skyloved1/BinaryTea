package sky.learnspringbinarytea.mybaits

import org.apache.ibatis.annotations.*
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler
import sky.learnspringbinarytea.mybaits.typehandler.SizeEnumTypeHandler


@Mapper
interface MenuItemMapper {
    @Select("select count(*) from t_menu")
    fun count(): Long

    @Insert(
        "insert into t_menu (name, price, size, create_time, update_time) " +
                "values (#{name}, #{price, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler}, #{size, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.SizeEnumTypeHandler}, now(), now())"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun save(menuItem: MenuItem?): Int

    @Update(
        "update t_menu set name = #{name}, price = #{price, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler}, size = #{size, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.SizeEnumTypeHandler}, update_time = now() " +
                "where id = #{id}"
    )
    fun update(menuItem: MenuItem?): Int

    @Select("select * from t_menu where id = #{id}")
    @Results(
        id = "menuItem",
        value = [
            Result(column = "id", property = "id", id = true),
            Result(column = "size", property = "size", typeHandler = SizeEnumTypeHandler::class),
            Result(column = "price", property = "price", typeHandler = MoneyTypeHandler::class),
            Result(column = "create_time", property = "createTime"),
            Result(column = "update_time", property = "updateTime")
        ]
    )
    fun findById(@Param("id") id: Long?): MenuItem?

    @Delete("delete from t_menu where id = #{id}")
    fun deleteById(@Param("id") id: Long?): Long?

    @Select("select * from t_menu")
    fun findAll(): List<MenuItem?>

    @Select("select m.* from t_menu m, t_order_item i where m.id = i.item_id and i.order_id = #{orderId}")
    fun findByOrderId(orderId: Long?): List<MenuItem?>?
}


