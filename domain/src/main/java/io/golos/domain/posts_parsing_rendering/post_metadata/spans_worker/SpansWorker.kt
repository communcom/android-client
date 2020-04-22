package io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker

import android.text.style.CharacterStyle

interface SpansWorker: SpansWorkerRead {
    fun removeSpan(span: CharacterStyle)

    fun appendSpan(span: CharacterStyle, interval: IntRange)
}