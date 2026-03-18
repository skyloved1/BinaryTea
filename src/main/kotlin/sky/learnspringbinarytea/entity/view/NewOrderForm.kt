package sky.learnspringbinarytea.entity.view

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class NewOrderForm(
    @NotEmpty
    var itemIdList: List<String>?=null,
    @Max(100)
    @Min(50)
    var discount: Int?=null
)
