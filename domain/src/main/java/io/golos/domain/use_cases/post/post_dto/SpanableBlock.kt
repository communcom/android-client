package io.golos.domain.use_cases.post.post_dto

import android.text.Spannable

data class SpanableBlock(
    val content: Spannable
) : ParagraphItemBlock