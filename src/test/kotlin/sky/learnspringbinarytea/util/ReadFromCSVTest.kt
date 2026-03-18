package sky.learnspringbinarytea.util

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import sky.learnspringbinarytea.entity.Size
import util.readFromCSV
import java.nio.charset.StandardCharsets
@SpringBootTest

class ReadFromCSVTest {

    @Test
    @Transactional
    fun testReadFromCSV() {
        // Prepare mock file content
        val content = "GreenTea 中杯 10.0\nBlackTea 大杯 15.0"
        val file = MockMultipartFile(
            "file", 
            "test.csv", 
            "text/plain", 
            content.toByteArray(StandardCharsets.UTF_8)
        )
        
        val result = readFromCSV(file)
        
        assertEquals(2, result.size)
        assertEquals("GreenTea", result[0].name)
        assertEquals(Size.中杯, result[0].size)
    }
}
