package io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom

import androidx.annotation.ColorInt
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.LinkInfo

class LinkSpan(
    data: LinkInfo,
    @ColorInt textColor: Int
) : SpecialSpansBase<LinkInfo>(data, textColor) {

    private val typeId = 2525358

    override fun getSpanTypeId(): Int = typeId
}