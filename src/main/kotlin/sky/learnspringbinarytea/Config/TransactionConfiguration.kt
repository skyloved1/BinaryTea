package sky.learnspringbinarytea.Config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class TransactionConfiguration {

    @Bean
    fun transactionManager(ds: DataSource) = DataSourceTransactionManager(ds)

}
