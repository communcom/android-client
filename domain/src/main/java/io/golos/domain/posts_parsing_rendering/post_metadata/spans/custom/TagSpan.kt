package io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom

import androidx.annotation.ColorInt

class TagSpan(
    value: String,
    @ColorInt textColor: Int
) : SpecialSpansBase<String>(value, textColor) {

    private val typeId = 365498

    override fun getSpanTypeId(): Int = typeId
}