package sky.learnspringbinarytea.mybaits

import org.apache.ibatis.annotations.*
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Order
import sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler
import sky.learnspringbinarytea.mybaits.typehandler.OrderStatusEnumTypeHandler

@Mapper
interface OrderMapper {
    @Select("select * from t_order where id = #{id}")
    @Results(
        id = "orderMap",
        value = [
            Result(column = "status", property = "status", typeHandler = OrderStatusEnumTypeHandler::class),
            // Map discount as plain integer to match Amount.discount type
            Result(column = "amount_discount", property = "amount.discount"),
            Result(column = "amount_total", property = "amount.totalAmount", typeHandler = MoneyTypeHandler::class),
            Result(column = "amount_pay", property = "amount.payAmount", typeHandler = MoneyTypeHandler::class),
            Result(
                column = "maker_id",
                property = "maker",
                one = One(select = "sky.learnspringbinarytea.mybaits.TeaMakerMapper.findById")
            ),
//            Result(
//                column = "id",
//                property = "items",
//                many = Many(select = "sky.learnspringbinarytea.mybaits.MenuItemMapper.findByOrderId")
//            ),
            Result(column ="id", property = "id", id = true )
        ]
    )
    fun findById(id: Long): Order?

    @Select("select * from t_order where maker_id = #{makerId}")
    @ResultMap("orderMap")
    fun findByMakerId(makerId: Long): List<Order?>

    @Insert(
        "insert into t_order (maker_id, amount_discount, amount_total, amount_pay, status, create_time, update_time) " +
                "values (#{maker.id}, #{amount.discount}, #{amount.totalAmount, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler}, #{amount.payAmount, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.MoneyTypeHandler}, #{status, typeHandler=sky.learnspringbinarytea.mybaits.typehandler.OrderStatusEnumTypeHandler}, now(), now())"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun save(order: Order): Int

    @Insert(
        "insert into t_order_item (order_id, item_id) values (#{orderId}, #{item.id})"
    )
    fun addOrderItem(orderId: Long,item: MenuItem): Int
}