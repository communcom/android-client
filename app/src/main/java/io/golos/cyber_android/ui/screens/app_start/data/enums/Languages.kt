package io.golos.cyber_android.ui.screens.app_start.data.enums

enum class Languages(val languageCode: String) {
    ENGLISH("en"),
    RUSSIAN("ru");

    companion object {
        fun getLanguageName(languageCode: String): Languages? = values().find {
            it.languageCode == languageCode
        }
    }

}