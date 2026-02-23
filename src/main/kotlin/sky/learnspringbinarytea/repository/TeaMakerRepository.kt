package sky.learnspringbinarytea.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sky.learnspringbinarytea.entity.TeaMaker
@Repository
interface TeaMakerRepository: JpaRepository<TeaMaker, Long> {
}