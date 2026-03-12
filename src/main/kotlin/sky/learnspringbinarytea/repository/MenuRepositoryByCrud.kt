package sky.learnspringbinarytea.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.entity.MenuItem
import sky.learnspringbinarytea.entity.Size

@Repository
interface  MenuRepositoryByCrud : JpaRepository<MenuItem, Long> {
    fun getMenuItemByNameAndSize(name: String, size: Size): MutableList<MenuItem>
}