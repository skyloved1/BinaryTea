package sky.learnspringbinarytea.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import org.joda.money.Money

@Embeddable
class Amount(
    @Column(name = "amount_discount")
    var discount: Int? = null,

    @Column(name = "amount_total")
    @Convert(converter = MoneyConverter::class)
    var totalAmount: Money? = null,
    @Column(name = "amount_pay")
    @Convert(converter = MoneyConverter::class)
    var payAmount: Money?= null
) {


}