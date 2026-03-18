package util

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import sky.learnspringbinarytea.entity.view.NewMenuItemForm
import sky.learnspringbinarytea.entity.Size

fun readFromCSV(file: MultipartFile): List<NewMenuItemForm> {
    val logger = LoggerFactory.getLogger("ReadFromCSV")
    val inputStream = file.inputStream
    return inputStream.bufferedReader(Charsets.UTF_8).useLines { sequence ->
        sequence.map { string ->
            val params = string.split(" ")
            if (params.size != 3) {
                logger.warn("Wrong number of params for $string")
                throw IllegalArgumentException("Wrong number of params for $string")
            }
            NewMenuItemForm(
                name = params[0],
                price = Money.of(CurrencyUnit.of("CNY"), params[2].toBigDecimal()),
                size = Size.valueOf(params[1])
            )
        }.toList()
    }
}