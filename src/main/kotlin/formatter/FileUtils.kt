package formatter

import java.io.File

object FileUtils {
    fun createFile(filePath: String, content: String) {
        val file = File(filePath)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }
        file.writeText(content)
    }

    fun readFile(filePath: String): List<String>? {
        val file = File(filePath)
        if (!file.exists()) return null
        return file.readLines()
    }
}