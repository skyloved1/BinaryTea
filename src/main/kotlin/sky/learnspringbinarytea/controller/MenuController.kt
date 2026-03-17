package sky.learnspringbinarytea.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import sky.learnspringbinarytea.cache.MenuService
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size

@Controller
@RequestMapping("/menu")
@ResponseBody
class MenuController(
    private val menuService: MenuService
) {
    @GetMapping(params = ["!name"])
    fun getAll()= menuService.getAllMenu()
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getById(@PathVariable id: Long): MenuItem? =menuService.getById(id)
    @GetMapping(params = ["name"])
    fun getByName(name: String)= menuService.getByName(name)


}
