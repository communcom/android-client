package io.golos.posts_editor.components.input.text_tasks

import android.net.Uri
import android.text.style.CharacterStyle
import android.util.Patterns
import android.webkit.URLUtil
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.LinkInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom.LinkSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker.SpansWorkerImpl
import io.golos.posts_editor.components.input.text_tasks.dto.TextSlice
import io.golos.posts_editor.utilities.post.spans.PostSpansFactory

/**
 * Finds and marks all links
 */
class LinksTask: TextTaskBase() {
    override fun findSlices(sourceText: CharSequence): List<TextSlice> {
        val result = mutableListOf<TextSlice>()

        val linksMatcher = Patterns.WEB_URL.matcher(sourceText)

        while (linksMatcher.find()) {
            if(URLUtil.isValidUrl(linksMatcher.group())) {
                result.add(TextSlice.LinkTextSlice(linksMatcher.group(), linksMatcher.start()..linksMatcher.end()))
            }
        }

        return result
    }

    override fun getAllSpans(spansWorker: SpansWorkerImpl): List<CharacterStyle> =
        spansWorker.getAllSpans<LinkSpan>(LinkSpan::class)

    override fun createSpan(text: String): CharacterStyle = PostSpansFactory.createLink(LinkInfo(text, Uri.parse(text)))
}