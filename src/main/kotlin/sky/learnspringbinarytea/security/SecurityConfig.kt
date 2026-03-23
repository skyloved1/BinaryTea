package sky.learnspringbinarytea.security

import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class SecurityConfig {
    val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { it.requestMatchers("/menu").permitAll() }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .formLogin({
                it.defaultSuccessUrl("/order")
                it.failureUrl("/login")
            })
            .rememberMe({
                it.key("binarytea-remember-me") // 设置一个密钥，用于签名 cookie
                it.tokenValiditySeconds(86400) // 记住我有效期 1 天 (86400秒)
            })
            .httpBasic(withDefaults())
        return http.build()
    }

    // 这里的密码编码器是为了演示而设置的，实际项目中应该使用更安全的密码存储方式
    /* @Bean
     fun userDetailsService(passwordEncoderProvider: ObjectProvider<PasswordEncoder>): UserDetailsService {
         val passwordEncoder = passwordEncoderProvider.getIfAvailable { passwordEncoder() }
         val employee = User.builder()
             .username("lilei")
             .password("binarytea")
             .passwordEncoder(passwordEncoder::encode)
             .roles("EMPLOYEE")
             .build()
         return InMemoryUserDetailsManager(employee)
     }
 */
    @Bean
    fun jdbcUserDetailsService(
        passwordEncoderProvider: ObjectProvider<PasswordEncoder>,
        dataSource: DataSource
    ): UserDetailsService {
        val passwordEncoder = passwordEncoderProvider.getIfAvailable { passwordEncoder() }
        val jdbcUserDetailsManager = JdbcUserDetailsManager(dataSource)
        val employee = User.builder()
            .username("lilei")
            .password("binarytea")
            .passwordEncoder(passwordEncoder::encode)
            .roles("EMPLOYEE")
            .build()
        runCatching {
            jdbcUserDetailsManager.createUser(employee)
        }.onFailure {
            // 如果用户已存在，可以忽略异常或打印日志
            if (it is DuplicateKeyException)
                logger.warn("User 'lilei' already exists in the database. Skipping user creation.")
            else {
                logger.error("Error creating user 'lilei': ${it.message}")
                throw it
            }
        }
        return jdbcUserDetailsManager
    }
}
