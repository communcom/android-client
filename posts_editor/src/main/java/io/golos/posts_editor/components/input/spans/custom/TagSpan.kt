package io.golos.posts_editor.components.input.spans.custom

import androidx.annotation.ColorInt
import io.golos.posts_editor.components.input.spans.custom.SpecialSpansBase

class TagSpan(
    value: String,
    @ColorInt textColor: Int
) : SpecialSpansBase<String>(value, textColor) {

    private val typeId = 365498

    override fun getSpanTypeId(): Int = typeId
}