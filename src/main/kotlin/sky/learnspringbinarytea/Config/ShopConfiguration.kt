package sky.learnspringbinarytea.Config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BinaryTeaProperties::class)
class ShopConfiguration {

}


@ConfigurationProperties(prefix = "binary-tea")
class BinaryTeaProperties(
    var ready: Boolean = false,
    var openHours: String = "8:00-20:00"
) {

}