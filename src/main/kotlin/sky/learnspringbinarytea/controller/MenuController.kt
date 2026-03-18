package sky.learnspringbinarytea.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sky.learnspringbinarytea.service.MenuService
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.view.NewMenuItemForm
import util.readFromCSV

@Controller
@RequestMapping("/menu")
@ResponseBody
class MenuController(
    private val menuService: MenuService,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping(params = ["!name"])
    fun getAll() = menuService.getAllMenu()

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getById(@PathVariable id: Long): MenuItem? = menuService.getById(id)

    @GetMapping(params = ["name"])
    fun getByName(name: String) = menuService.getByName(name)

    @PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun createByForm(
        @Valid form: NewMenuItemForm,
        result: BindingResult,
        httpServletResponse: HttpServletResponse
    ): MenuItem? {
        if (result.hasErrors()) {
            logger.warn(
                "Invalid form data: {}",
                result.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" })
            httpServletResponse.status = HttpStatus.BAD_REQUEST.value()
            return null;
        }
        return menuService.save(form);
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createByBatched(
        @RequestParam file: MultipartFile,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<List<MenuItem>> {
        try {
            val items = readFromCSV(file).map { form ->
                MenuItem(
                    name = form.name,
                    size = form.size,
                    price = form.price,
                )
            }
            return ResponseEntity.ok(menuService.saveAll(items))
        } catch (e: IllegalArgumentException) {
            httpServletResponse.status = HttpStatus.BAD_REQUEST.value()
            throw e
        }

    }
}
