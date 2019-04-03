package io.golos.domain

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-03.
 */
object Regexps {

    val hashTagRegexp by lazy { "(?<=\\s|^)#[A-Za-z0-9_-]{2,32}\\b".toRegex() }
    val link by lazy { "(https://||http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?+%/.\\w@a]+)".toRegex() }
}