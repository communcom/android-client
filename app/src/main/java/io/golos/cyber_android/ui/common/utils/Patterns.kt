package io.golos.cyber_android.ui.common.utils

import io.golos.domain.Regexps
import java.util.regex.Pattern

object Patterns {
    val HASHTAG: Pattern = Regexps.hashTagRegexp.toPattern()
    val WEB_URL: Pattern = Regexps.link.toPattern()
    val USERNAME: Pattern = "(?<=\\s|^)@[A-Za-z0-9_-]{2,32}\\b".toPattern()
}