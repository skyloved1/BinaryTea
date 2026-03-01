package sky.learnspringbinarytea.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class TransactionManagerConfiguration {
    @Bean
    @Primary
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager = JpaTransactionManager(emf)
    /*    @Bean //与JPA的事务管理器冲突，暂时注释掉
        @ConditionalOnMissingBean(PlatformTransactionManager::class)
        fun transactionManager(ds: DataSource): PlatformTransactionManager = DataSourceTransactionManager(ds)*/

}