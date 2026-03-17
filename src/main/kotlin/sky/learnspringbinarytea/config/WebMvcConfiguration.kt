package sky.learnspringbinarytea.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

///

class WebMvcConfiguration {
    /// 通过实现 WebMvcRegistrations 接口，我们可以自定义 Spring MVC 的配置，例如添加拦截器、消息转换器等。

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

    fun objectMapperCustomizer() = JsonMapperBuilderCustomizer { jsonMapperBuilder ->
        jsonMapperBuilder.findAndAddModules()
    }
}
