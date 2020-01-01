package io.golos.cyber_android.ui.shared.utils

import org.junit.Assert
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class FromSpannedToHtmlTransformerImplTest {
    val htmlizer = FromSpannedToHtmlTransformerImpl()

    @Test
    fun transform() {
        var text = "<p dir=\\\"ltr\\\"><b>vbjdktidppoq</b> 1231</p>\\n"

        var cleaned = htmlizer.cleanHtml(text)

        Assert.assertEquals("<b>vbjdktidppoq</b> 1231", cleaned)

        text = "<b>vbjdktidppoq</b> ht as as as as asfasfasf asfas1 afasf<p></p><p>"
        cleaned = htmlizer.cleanHtml(text)
        Assert.assertEquals("<b>vbjdktidppoq</b> ht as as as as asfasfasf asfas1 afasf", cleaned)
    }
}