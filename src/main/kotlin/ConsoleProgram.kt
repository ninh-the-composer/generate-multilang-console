import exception.NoKeyException
import formatter.DateUtils
import formatter.FileUtils
import formatter.Formatter
import formatter.Formatter.appendResourcesForAndroid
import formatter.Formatter.generateStringResourcesForAndroid
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import model.FormatType
import model.Language
import model.ResourcesFile
import java.io.FileNotFoundException

object ConsoleProgram {
    private val _timeCreated by lazy { DateUtils.getCurrentDate("ddMMyy_HH'h'mm'm'ss") }

    operator fun invoke() = runBlocking {
        val deferredResources = async {
            Formatter.formatExcelFile(
                supportedLanguages = Language.entries,
                filePath = Constants.MULTI_LANGUAGES_XLSX_NAME
            )
        }
        println("Tool auto generate multi-languages!")
        askPlatform()
        val optionFormatType = readlnOrNull()
        val formatType = when (optionFormatType) {
            "1" -> FormatType.Android
            "2" -> FormatType.IOS
            else -> {
                println("Bye~")
                return@runBlocking
            }
        }
        askMethod(formatType)
        val optionMethod = readlnOrNull()
        val isAppendCurrent = optionMethod == "1"
        println("In a minute!")
        val resources = try {
            deferredResources.await()
        } catch (ex: NoKeyException) {
            error("File multi languages has no column key!")
        } catch (ex: FileNotFoundException) {
            error("File multi languages was not found!")
        }catch (ex: Exception) {
            error("File multi languages wrong format!")
        }
        when (formatType) {
            FormatType.Android -> generateStringResourcesAndroid(isAppendCurrent, resources)
            FormatType.IOS -> generateStringResourcesIOS(isAppendCurrent, resources)
        }
        noticeFinishJob(formatType)
    }

    private fun generateStringResourcesIOS(isAppendCurrent: Boolean, resources: List<ResourcesFile>) {
        TODO("Not supported yet")
    }

    private fun generateStringResourcesAndroid(isAppendCurrent: Boolean, resources: List<ResourcesFile>) {
        if (isAppendCurrent) {
            doAppendCurrentAndroidResources(resources)
        } else {
            createNewAndroidResources(resources)
        }
    }

    private fun doAppendCurrentAndroidResources(newResources: List<ResourcesFile>) {
        val existedResources = Formatter.readAndroidResourcesFiles(Language.entries)
        existedResources.forEach { existedResource ->
            val newStringResources =
                newResources.firstOrNull { resourcesFile -> resourcesFile.language == existedResource.language }
                    ?: return@forEach
            if (newStringResources.resources.isEmpty()) return@forEach
            val fileContent = existedResource.resources.appendResourcesForAndroid(newStringResources.resources)
            val locale = if (existedResource.language != Language.EN) {
                "-${existedResource.language.locale}"
            } else {
                ""
            }
            val folderName = "output/${_timeCreated}"
            FileUtils.createFile(
                filePath = "./${FormatType.Android.directory}/$folderName/value$locale/${FormatType.Android.fileName}",
                content = fileContent
            )
        }
    }

    private fun createNewAndroidResources(newResources: List<ResourcesFile>) {
        newResources.forEach { resource ->
            if (resource.resources.isEmpty()) return@forEach
            val fileContent = resource.generateStringResourcesForAndroid()
            val locale = if (resource.language != Language.EN) "-${resource.language.locale}" else ""
            val folderName = "output/${_timeCreated}"
            FileUtils.createFile(
                filePath = "./${FormatType.Android.directory}/$folderName/value$locale/${FormatType.Android.fileName}",
                content = fileContent
            )
        }
    }

    private fun askPlatform() {
        println("> Which platform?")
        println("1. Android")
        println("2. IOS")
        println("Q. Exit")
        print("> ")
    }

    private fun askMethod(type: FormatType) {
        println("> Do you want to append to current string resources of your project?")
        println("Your current multi languages should be in ./${type.directory}/current directory")
        println("1. Yes")
        println("2. No")
        println("Q. Exit")
        print("> ")
    }

    private fun noticeFinishJob(type: FormatType) {
        val folderName = "output/${_timeCreated}"
        println("Done!")
        println("Your output appear in folder ./${type.directory}/$folderName")
    }
}