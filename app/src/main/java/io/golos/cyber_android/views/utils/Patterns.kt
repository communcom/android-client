package io.golos.cyber_android.views.utils

import io.golos.domain.Regexps
import java.util.regex.Pattern

object Patterns {
    val HASHTAG: Pattern = Regexps.hashTagRegexp.toPattern()
    val WEB_URL: Pattern = Regexps.link.toPattern()
}