package sky.learnspringbinarytea.entity.view

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.joda.money.Money
import sky.learnspringbinarytea.entity.Size

data class NewMenuItemForm(
    @NotBlank
    val name: String?,
    @NotNull
    var price: Money?,
    @NotNull
    var size: Size?
)