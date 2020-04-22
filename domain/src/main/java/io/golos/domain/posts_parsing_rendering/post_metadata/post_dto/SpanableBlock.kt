package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import android.text.Spannable

data class SpanableBlock(
    val content: Spannable
) : ParagraphItemBlock