package io.golos.domain

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-04.
 */
interface HtmlToSpannableTransformer {
    fun transform(html: String): CharSequence
}

interface FromSpannedToHtmlTransformer {
    fun transform(spanned: CharSequence): String
}