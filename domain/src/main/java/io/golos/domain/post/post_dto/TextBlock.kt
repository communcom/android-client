package io.golos.domain.post.post_dto

import androidx.annotation.ColorInt
import io.golos.domain.post.TextStyle

data class TextBlock(
    val content: String,
    val style: TextStyle?,
    @ColorInt val textColor: Int?
): ParagraphItemBlock