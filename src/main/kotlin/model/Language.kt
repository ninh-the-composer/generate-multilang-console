package model

enum class Language(val locale: String, val columnKey: String) {
    EN("en", "English"),
    VI("vi", "Vietnamese"),
    JP("jp", "Japanese"),
    KR("kr", "Korean")
}
