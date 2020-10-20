package io.golos.domain.posts_parsing_rendering.post_metadata.spans.calculators

import android.text.style.CharacterStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.SpanInfo

sealed class SpanOperation

data class DeleteSpanOperation(val span: CharacterStyle): SpanOperation()

data class CreateSpanOperation<T>(val spanInfo: SpanInfo<T>): SpanOperation()