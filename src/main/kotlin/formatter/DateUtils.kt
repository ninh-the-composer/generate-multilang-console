package formatter

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    fun getCurrentDate(formatPattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(formatPattern)
        return LocalDateTime.now().format(formatter)
    }
}