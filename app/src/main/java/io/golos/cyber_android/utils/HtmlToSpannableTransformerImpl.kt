package io.golos.cyber_android.utils

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import androidx.annotation.VisibleForTesting
import io.golos.domain.FromSpannedToHtmlTransformer
import io.golos.domain.HtmlToSpannableTransformer
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-04.
 */
class HtmlToSpannableTransformerImpl
@Inject
constructor() : HtmlToSpannableTransformer {
    override fun transform(source: String): CharSequence {
        if(source.startsWith("{")) {        // It's json
            return source
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, FROM_HTML_MODE_LEGACY)
        }
        else {
            Html.fromHtml(source)
        }
    }
}

class FromSpannedToHtmlTransformerImpl
@Inject
constructor() : FromSpannedToHtmlTransformer {
    private val firstParagraphRegexp = "^<p[\\w\\s=\\\\\"]*>".toRegex()
    private val emptyParagraphs = "<p>[\\s]*</p>".toRegex()
    private val endingParagrapg = "<[/]?p>\$".toRegex()

    override fun transform(spanned: CharSequence): String {

        return (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.toHtml(spanned as Spanned, FROM_HTML_MODE_LEGACY)
        else Html.toHtml(spanned as Spanned)).let {
            cleanHtml(it)
        }
    }

    @VisibleForTesting
    fun cleanHtml(htmlIn: String): String {
        println("in html = $htmlIn")
        return htmlIn
            .removePrefix("\\n")
            .removeSuffix("\\n")
            .trim()
            .replace(firstParagraphRegexp, "")
            .replace(emptyParagraphs, "")
            .replace(endingParagrapg, "")
    }
}