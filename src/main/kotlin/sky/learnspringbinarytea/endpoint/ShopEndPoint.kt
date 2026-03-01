package sky.learnspringbinarytea.endpoint

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.stereotype.Component
import sky.learnspringbinarytea.config.BinaryTeaProperties
@Component
@Endpoint(id = "shop")
class ShopEndPoint(propertiesProvider: ObjectProvider<BinaryTeaProperties>) {

    val properties: BinaryTeaProperties? = propertiesProvider.getIfAvailable()

    @ReadOperation
    fun state(): String{
        val ready= properties?.ready ?: false
        return if (ready) "Shop is open: ${properties!!.openHours}" else "Shop is Closed"
    }
}