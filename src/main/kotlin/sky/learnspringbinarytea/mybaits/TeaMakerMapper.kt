package sky.learnspringbinarytea.mybaits

import org.apache.ibatis.annotations.Many
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.Select
import sky.learnspringbinarytea.entity.TeaMaker

@Mapper
interface TeaMakerMapper {

    @Select("select * from t_tea_maker where id = #{id}")
    @Results(
        id = "teaMakerMap",
        value = [
            Result(column = "id", property = "id"),
            Result(column = "id", property = "orders", many = Many(select = "sky.learnspringbinarytea.mybaits.OrderMapper.findByMakerId")),
        ]
    )
    fun findById(id: Long): TeaMaker?
}