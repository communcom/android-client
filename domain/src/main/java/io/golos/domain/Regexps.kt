package io.golos.domain

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-03.
 */
object Regexps {

    val hashTagRegexp by lazy { "(?<=\\s|^)#[A-Za-z0-9_-]{2,32}\\b".toRegex() }
    val link by lazy { "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})".toRegex() }
}