package model

enum class FormatType(val fileName: String, val directory: String) {
    Android("string.xml", "android"),
    IOS("", "ios") //todo: ask some ios dudes
}