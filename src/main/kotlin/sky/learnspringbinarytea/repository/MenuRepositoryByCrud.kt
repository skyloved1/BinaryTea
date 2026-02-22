package sky.learnspringbinarytea.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.entity.MenuItem

@Repository
interface  MenuRepositoryByCrud : JpaRepository<MenuItem, Long> {
}