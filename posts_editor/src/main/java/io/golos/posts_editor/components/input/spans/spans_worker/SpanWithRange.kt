package io.golos.posts_editor.components.input.spans.spans_worker

import android.text.style.CharacterStyle

data class SpanWithRange<T: CharacterStyle> (val span: T, val spanInterval: IntRange)