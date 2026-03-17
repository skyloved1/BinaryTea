package sky.learnspringbinarytea.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import sky.learnspringbinarytea.serializer.MoneyReadder

@Configuration
class WebMvcConfiguratio {


    @Bean
    fun webMvcRegistration() = object : WebMvcRegistrations {
        override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping? {
            return super.getRequestMappingHandlerMapping()
        }

        override fun getRequestMappingHandlerAdapter(): RequestMappingHandlerAdapter? {
            return super.getRequestMappingHandlerAdapter()
        }

        override fun getExceptionHandlerExceptionResolver(): ExceptionHandlerExceptionResolver? {
            return super.getExceptionHandlerExceptionResolver()
        }
    }

    @Bean
    fun objectMapperCustomizer() = JsonMapperBuilderCustomizer { jsonMapperBuilder ->
        jsonMapperBuilder.findAndAddModules()
    }
}
