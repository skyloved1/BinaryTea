package sky.learnspringbinarytea.model

import java.math.BigDecimal
import java.util.Date


data class MenuItem(
    var id: Long,
    var name: String,
    var price: BigDecimal,
    var size: String,
    var createTime: Date,
    var updateTime: Date
){

}
