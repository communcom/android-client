package io.golos.domain.use_cases.post.post_dto

import androidx.annotation.ColorInt
import io.golos.domain.use_cases.post.TextStyle

data class TextBlock(
    val id: String?,
    val content: String,
    val style: TextStyle?,
    @ColorInt
    val textColor: Int?
) : ParagraphItemBlock