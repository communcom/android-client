package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import androidx.annotation.ColorInt
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle

data class TextBlock(
    val id: Long?,
    val content: String,
    val style: TextStyle?,
    @ColorInt
    val textColor: Int?
) : ParagraphItemBlock