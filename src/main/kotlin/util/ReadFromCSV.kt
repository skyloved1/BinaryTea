package util

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import sky.learnspringbinarytea.entity.NewMenuItemForm
import sky.learnspringbinarytea.entity.Size

fun readFromCSV(file: MultipartFile): List<NewMenuItemForm> {
    val logger = LoggerFactory.getLogger("ReadFromCSV")
    val inputStream = file.inputStream
    val items = mutableListOf<NewMenuItemForm>()
    inputStream.bufferedReader(Charsets.UTF_8).useLines { sequence ->
        sequence.forEachIndexed { index, string ->
            println("$index: $string")
            val params = string.split(" ")
            if (params.size !in 3..3) {
                logger.warn("Wrong number of params for $string")
                throw IllegalArgumentException("Wrong number of params for $string")
            }
            items.add(
                NewMenuItemForm(
                    name = params[0],
                    price = Money.of(CurrencyUnit.of("CNY"), params[2].toBigDecimal()),
                    size = Size.valueOf(params[1])
                )
            )
        }
    }
    return items;
}