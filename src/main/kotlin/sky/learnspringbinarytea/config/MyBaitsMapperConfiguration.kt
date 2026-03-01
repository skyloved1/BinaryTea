package sky.learnspringbinarytea.config

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Configuration

@Configuration
@MapperScan("sky.learnspringbinarytea.mybaits")
class MyBaitsMapperConfiguration {

}
