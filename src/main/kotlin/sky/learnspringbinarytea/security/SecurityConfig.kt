package sky.learnspringbinarytea.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import sky.learnspringbinarytea.jwt.JwtAuthenticationFilter
import sky.learnspringbinarytea.jwt.JwtTokenHelper
import tools.jackson.databind.ObjectMapper
import javax.sql.DataSource


@Configuration
class SecurityConfigForBean {


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean("authenticationManager")
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Suppress("removal")
    @Bean
    fun persistentTokenRepository(dataSource: DataSource) = JdbcTokenRepositoryImpl().apply {
        setCreateTableOnStartup(false)
        setDataSource(dataSource)
    }
}


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val persistentTokenRepository: JdbcTokenRepositoryImpl,
) {
    val logger = LoggerFactory.getLogger(this::class.java)


    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        objectMapper: ObjectMapper, jwtTokenHelper: JwtTokenHelper, authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        http
            //ddFilterAt(...)
            //把你的 jwtAuthenticationFilter 放到 AbstractPreAuthenticatedProcessingFilter
            // 这个位置上。意思是：用你自定义 JWT 逻辑替换/占据该过滤器槽位，在认证链中按这个阶段执行。
            .addFilterAt(
                jwtAuthenticationFilter(jwtTokenHelper, authenticationManager = authenticationManager),
                AbstractPreAuthenticatedProcessingFilter::class.java
            )
            //配置“未认证时怎么响应”，并按请求类型分流：
            .exceptionHandling { handlingConfigurer ->
                //TEXT_HTML：走 LoginUrlAuthenticationEntryPoint("/login")，浏览器请求会重定向到 /login
                handlingConfigurer
                    //TEXT_HTML：走 LoginUrlAuthenticationEntryPoint("/login")，浏览器请求会重定向到 /login
                    .defaultAuthenticationEntryPointFor(
                        LoginUrlAuthenticationEntryPoint("/login"),
                        MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
                    // APPLICATION_JSON：走 HttpStatusEntryPoint(401)，API 请求直接返回 401 Unauthorized
                    .defaultAuthenticationEntryPointFor(
                        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                    )
            }
            .authorizeHttpRequests {
                it.requestMatchers("/menu").permitAll()
                it.anyRequest().authenticated()
            }
            .formLogin({
                it.defaultSuccessUrl("/order")
                it.failureUrl("/login")
                it.failureHandler { request, response, exception ->
                    val jsonResponse = mapOf(
                        "timestamp" to System.currentTimeMillis(),
                        "status" to 401,
                        "error" to "Unauthorized",
                        "message" to exception.message,
                        "path" to request.requestURI
                    )
                    response.contentType = "application/json;charset=UTF-8"
                    // 告诉浏览器 3 秒后自动跳转到 /login
                    response.setHeader("Refresh", "3;url=/login")
                    response.writer.write(objectMapper.writeValueAsString(jsonResponse))
                }
            })
            .rememberMe({
                it.key("binarytea-remember-me") // 设置一个密钥，用于签名 cookie
                it.tokenValiditySeconds(86400) // 记住我有效期 1 天 (86400秒)
                it.tokenRepository(persistentTokenRepository)
            })
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/login")
                it.invalidateHttpSession(true)
                it.deleteCookies("JSESSIONID", "remember-me") // 删除 session 和 remember-me cookie
                it.logoutRequestMatcher { request ->
                    (request.method == "POST" || request.method == "GET") && request.requestURI == "/logout"
                }
            }
            .csrf { it.disable() }
            .anonymous(withDefaults())
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
        val passwordEncoder = passwordEncoderProvider.getIfAvailable { SecurityConfigForBean().passwordEncoder() }
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

    @Bean
    fun jwtAuthenticationFilter(
        jwtTokenHelper: JwtTokenHelper,
        authenticationManager: AuthenticationManager
    ): JwtAuthenticationFilter {
        val filter = JwtAuthenticationFilter(
            jwtTokenHelper = jwtTokenHelper
        ).apply { setAuthenticationManager(authenticationManager) }
        return filter
    }

    @Bean
    fun jwtPreAuthenticateAuthenticationProvider(userDetailsService: UserDetailsService) =
        PreAuthenticatedAuthenticationProvider().apply {
            setPreAuthenticatedUserDetailsService(UserDetailsByNameServiceWrapper(userDetailsService))
        }
}
