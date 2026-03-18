package sky.learnspringbinarytea.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sky.learnspringbinarytea.entity.MenuItem
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
    fun createNewOrder(@Valid orderForm: NewOrderForm, result: BindingResult, modelMap: ModelMap): String {
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
        modelMap.addAttribute("orders",orderService.getAllOrders())
        return "order"
    }
}
