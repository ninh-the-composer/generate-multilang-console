package formatter

import exception.NoKeyException
import model.Language
import model.ResourcesFile
import model.StringResource
import model.ExistedResources
import model.FormatType
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convertToString
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.io.readExcel

object Formatter {
    fun formatExcelFile(supportedLanguages: List<Language>, filePath: String): List<ResourcesFile> {
        val excel = DataFrame.readExcel(filePath)
        val keys = excel.getColumnOrNull("key")?.convertToString()?.toList()
        if (keys.isNullOrEmpty()) throw NoKeyException()
        return supportedLanguages.map { language ->
            val resources = excel.getColumnOrNull(language.columnKey)?.convertToString()
                ?.mapIndexed { index: Int, resource: String? ->
                    StringResource(
                        key = keys[index],
                        string = resource
                    )
                }?.toList().orEmpty()
            ResourcesFile(
                language = language,
                resources = resources
            )
        }
    }


    fun readAndroidResourcesFiles(supportedLanguages: List<Language>): List<ExistedResources> {
        return supportedLanguages.map { language ->
            val locale = if (language != Language.EN) "-${language.locale}" else ""
            val resourceLines = FileUtils.readFile(
                filePath = "${FormatType.Android.directory}/current/value$locale/${FormatType.Android.fileName}"
            )
            ExistedResources(
                language = language,
                resources = resourceLines.orEmpty()
            )
        }
    }

    fun List<String>.appendResourcesForAndroid(newStringResources: List<StringResource>): String {
        if (this.isEmpty()) return ""
        val indexOfCloseTag = this.indexOfFirst { line ->
            line.contains("</resources>")
        }
        val afterCloseTag = this.subList(indexOfCloseTag, this.size)
        val beforeCloseTag = this.take(indexOfCloseTag)
        val newResourceLines = newStringResources.generateResourcesStringLinesForAndroid()
        val newResources = newResourceLines.joinToString(
            prefix = "\t",
            separator = "\n\t"
        )
        val linesMerged = beforeCloseTag + newResources + afterCloseTag
        return linesMerged.joinToString(separator = "\n")
    }

    fun ResourcesFile.generateStringResourcesForAndroid(): String {
        val resourceStringLines = resources.generateResourcesStringLinesForAndroid()
        return resourceStringLines.joinToString(
            prefix = "<resources>\n\t",
            separator = "\n\t",
            postfix = "\n</resources>"
        )
    }

    private fun List<StringResource>.generateResourcesStringLinesForAndroid(): List<String> {
        return this.map { resource -> resource.generateLinesForAndroid() }
    }

    private fun StringResource.generateLinesForAndroid(translatable: Boolean = true) =
        "<string name=\"$key\"${if (translatable) "" else " translatable=\"false\""}>$string</string>"
}