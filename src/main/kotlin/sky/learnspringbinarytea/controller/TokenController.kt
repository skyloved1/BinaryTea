package sky.learnspringbinarytea.controller


import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import sky.learnspringbinarytea.entity.view.TokenRequest
import sky.learnspringbinarytea.entity.view.TokenResponse
import sky.learnspringbinarytea.jwt.JwtTokenHelper

@Controller
@RequestMapping("/token")
class TokenController(
    val authenticationManager: AuthenticationManager,
    val jwtTokenHelper: JwtTokenHelper,

    ) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createToken(
        @RequestBody @Valid tokenRequest: TokenRequest,
        result: BindingResult
    ): ResponseEntity<TokenResponse> {
        if (result.hasErrors()) {
            val errorMessages = result.allErrors.joinToString(",") { it.defaultMessage.toString() }
            return ResponseEntity.badRequest().body(TokenResponse(message = errorMessages))
        }
        runCatching {
            UsernamePasswordAuthenticationToken(tokenRequest.username, tokenRequest.password).let {
                authenticationManager.authenticate(it)
            }
        }.onFailure {
            when (it) {
                is AuthenticationException -> {
                    logger.warn("Login failed. User: ${tokenRequest.username},Reason: ${it.message}")
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(TokenResponse(message = it.message ?: "Unknown error"))
                }

                else -> return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse(message = "Unknown error"))
            }
        }.onSuccess {
            return ResponseEntity.ok(TokenResponse(generateToken(tokenRequest.username), message = null))
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TokenResponse(message = "Unknown error"))
    }

    private fun generateToken(username: String) = jwtTokenHelper.generateToken(username).also {
        logger.info("为用户${username}生成了Token: [$it]")

    }


}
