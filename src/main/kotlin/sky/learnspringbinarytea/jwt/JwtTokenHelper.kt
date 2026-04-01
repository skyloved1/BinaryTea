package sky.learnspringbinarytea.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenHelper : InitializingBean {
    companion object {
        const val ISSUER = "BinaryTea"

    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var jwtParser: JwtParser


    private lateinit var key: SecretKey

    @Value($$"${jwt.secret}")
    fun setBase64Key(base64Key: String) {
        //  key = Keys.builder(SecretKeySpec(Decoders.BASE64.decode(base64Key), Jwts.SIG.HS512.id)).build()
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Key))
    }

    override fun afterPropertiesSet() {
        jwtParser = Jwts.parser().apply {
            requireIssuer(ISSUER)
            verifyWith(key)
            clockSkewSeconds(10)
        }.build()

    }


    fun generateToken(username: String): String {
        val now = LocalDateTime.now()
        val expireTime = now.plus(Duration.ofHours(1))

        return Jwts.builder()
            .subject(username)
            .issuer(ISSUER)
            .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .expiration(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    fun parseToken(token: String): Result<Jws<Claims>> = runCatching {
        return Result.success(jwtParser.parseSignedClaims(token))
    }.onFailure {
        when (it) {
            is SignatureException, is MalformedJwtException, is UnsupportedJwtException, is IllegalArgumentException -> Result.failure<Throwable>(
                BadCredentialsException(
                    "Invalid Token",
                    it
                )
            )

            is ExpiredJwtException -> Result.failure(
                CredentialsExpiredException(
                    "Token Expired",
                    it
                )
            )

            else -> Result.failure(UnknownError("Unknown error while parsing token: ${it.message}"))
        }
    }


}
