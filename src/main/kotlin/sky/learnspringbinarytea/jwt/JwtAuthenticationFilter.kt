package sky.learnspringbinarytea.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class JwtAuthenticationFilter(
    private val jwtTokenHelper: JwtTokenHelper
) : AbstractPreAuthenticatedProcessingFilter() {
    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        //如果没有Bearer头说明还没有获取token,或者格式不对，直接返回null，交给后续的过滤器处理（比如匿名用户）
        if (header.isNullOrBlank() || !header.startsWith("Bearer ")) return null
        val token = header.substringAfter("Bearer ").trim()
        val jws = jwtTokenHelper.parseToken(token)
        jws
            //具体是：
            // getPreAuthenticatedPrincipal() 返回 subject
            //AbstractPreAuthenticatedProcessingFilter 拿到它，组装预认证 token
            //交给 PreAuthenticatedAuthenticationProvider
            //provider 再用 UserDetailsService 加载用户权限
            //最终放入 SecurityContext
            .onSuccess { return it.payload.subject }
            .onFailure {
                logger.warn("Invalid JWT token: ${it.message}")
                return null
            }
        return null//理论上不会运行到这里
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
        return "NO_PASSWORD_NEEDED"
    }
}