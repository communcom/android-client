package io.golos.posts_editor.components.input.spans.calculators

import android.text.style.CharacterStyle
import io.golos.domain.post.editor_output.SpanInfo

sealed class SpanOperation

data class DeleteSpanOperation(val span: CharacterStyle): SpanOperation()

data class CreateSpanOperation<T>(val spanInfo: SpanInfo<T>): SpanOperation()