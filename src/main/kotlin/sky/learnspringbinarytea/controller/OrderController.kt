package sky.learnspringbinarytea.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Order
import sky.learnspringbinarytea.entity.view.NewOrderForm
import sky.learnspringbinarytea.service.MenuService
import sky.learnspringbinarytea.service.OrderService

@Controller
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val menuService: MenuService
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ModelAttribute("items")
    fun items() = menuService.getAllMenu()

    @GetMapping
    fun orderPage() = ModelAndView("order").addObject(
        NewOrderForm()
    ).addObject("orders", orderService.getAllOrders())

    @PostMapping
    fun createNewOrderWithForm(@Valid orderForm: NewOrderForm, result: BindingResult, modelMap: ModelMap): String {
        if (result.hasErrors()) {
            modelMap.addAttribute("orders", orderService.getAllOrders())
            return "order"
        }
        val itemList = orderForm.itemIdList
            ?.map { str -> str.toLong() }
            ?.map { longId -> menuService.getById(longId) } ?: emptyList<MenuItem>()
        val order = orderService.createOrder(
            itemList = itemList,
            discount = orderForm.discount ?: 100
        ).also { order -> logger.info("创建新订单,Order={}", order) }
        modelMap.addAttribute("orders", orderService.getAllOrders())
        return "order"
    }

    @PostMapping("json", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createNewOrderWithJson(@RequestBody @Valid orderForm: NewOrderForm, result: BindingResult): ResponseEntity<Order> {
        if (result.hasErrors()) {
            logger.warn(
                "Invalid order form data: {}",
                result.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" })
            throw IllegalArgumentException("不能创建订单，数据不合法: ${result.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }}")
        }
        val itemList = orderForm.itemIdList
            ?.map { str -> str.toLong() }
            ?.map { longId -> menuService.getById(longId) } ?: emptyList<MenuItem>()
        val order = orderService.createOrder(
            itemList = itemList,
            discount = orderForm.discount ?: 100
        ).also { order -> logger.info("创建新订单,Order={}", order) }
        return ResponseEntity.ok(order)
    }
}
