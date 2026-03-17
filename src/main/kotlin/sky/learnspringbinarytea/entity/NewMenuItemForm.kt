package sky.learnspringbinarytea.entity

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.joda.money.Money

data class NewMenuItemForm(
    @NotBlank
    val name: String?,
    @NotNull
    var price: Money?,
    @NotNull
    var size: Size?
)
