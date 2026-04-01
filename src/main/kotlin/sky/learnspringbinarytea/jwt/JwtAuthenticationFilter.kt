package sky.learnspringbinarytea.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class JwtAuthenticationFilter(
    private val jwtTokenHelper: JwtTokenHelper
) : AbstractPreAuthenticatedProcessingFilter() {
    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!header.startsWith("Bearer ")) return null
        val token = header.substringAfter("Bearer ").trim()
        val jws = jwtTokenHelper.parseToken(token)
        jws
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