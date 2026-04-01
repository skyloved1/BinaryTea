package sky.learnspringbinarytea.entity.view

import jakarta.validation.constraints.NotEmpty

data class TokenRequest(
    @NotEmpty
    val username: String,
    @NotEmpty
    val password: String
)
