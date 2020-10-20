package io.golos.cyber_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.StringBuilder

@RunWith(AndroidJUnit4::class)
class VideoHtmlCorrectorTest {
    @Test
    fun test() {
        val openTag = "<iframe"
        val closeTag = "</iframe>"

        val builder1 = StringBuffer("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/gEZ1YK-peVM\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>")
        val builder2 = StringBuilder("<div><div style=\"left: 0; width: 100%; height: 0; position: relative; padding-bottom: 56.2493%;\"><iframe src=\"https://www.youtube.com/embed/UiYlRkVxC_4?feature=oembed\" style=\"border: 0; top: 0; left: 0; width: 100%; height: 100%; position: absolute;\" allowfullscreen scrolling=\"no\"></iframe></div></div>")

        val index1 = builder1.indexOf(openTag)            // must be 0
        val index2 = builder2.indexOf(openTag)

        val cut1 = builder2.removeRange(0 until builder2.indexOf(openTag))          // a head is cut
        val cut2 = cut1.removeRange(cut1.indexOf(closeTag)+closeTag.length until cut1.length)     // a tail is cut



        return
    }
}

// <div><div style="left: 0; width: 100%; height: 0; position: relative; padding-bottom: 56.2493%;"><iframe src="https://www.youtube.com/embed/UiYlRkVxC_4?feature=oembed" style="border: 0; top: 0; left: 0; width: 100%; height: 100%; position: absolute;" allowfullscreen scrolling="no"></iframe></div></div>
//val html = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/gEZ1YK-peVM\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
