package io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker

import android.text.style.CharacterStyle

data class SpanWithRange<T: CharacterStyle> (val span: T, val spanInterval: IntRange)