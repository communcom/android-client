package io.golos.cyber_android.utils

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import io.golos.domain.HtmlToSpannableTransformer

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-04.
 */
class HtmlToSpannableTransformerImpl : HtmlToSpannableTransformer {
    override fun transform(html: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(html, FROM_HTML_MODE_LEGACY)
        else Html.fromHtml(html)
    }
}