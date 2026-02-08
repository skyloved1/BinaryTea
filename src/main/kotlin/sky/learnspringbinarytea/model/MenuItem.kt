package sky.learnspringbinarytea.model

import java.math.BigDecimal
import java.util.Date


data class MenuItem(
    var id: Long?,// id 字段为可空类型，插入前为 null，插入成功后会被填充
    var name: String,
    var price: BigDecimal,
    var size: String,
    var createTime: Date?,// createTime 字段为可空类型，插入前为 null，插入成功后会被填充
    var updateTime: Date?// updateTime 字段为可空类型，插入前为 null，插入成功后会被填充
){

}
