package io.golos.cyber_android.ui.screens.profile.edit.settings.language

import androidx.annotation.StringRes
import io.golos.cyber_android.R

val supportedLanguages: List<LanguageOption> =
    listOf(
        LanguageOption("ru", R.string.russian),
        LanguageOption("en", R.string.english)
    )

data class LanguageOption(val code: String, @StringRes val displayedName: Int)