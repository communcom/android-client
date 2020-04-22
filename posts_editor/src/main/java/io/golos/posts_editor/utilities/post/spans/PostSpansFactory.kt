package io.golos.posts_editor.utilities.post.spans

import android.graphics.Color
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.LinkInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.toTypeface
import io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom.LinkSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom.MentionSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom.TagSpan

object PostSpansFactory {
    @ColorInt
    private val specialSpansColors = Color.BLUE

    fun createTextColor(@ColorInt color: Int): CharacterStyle = ForegroundColorSpan(color)

    fun createTextStyle(style: TextStyle): CharacterStyle = StyleSpan(style.toTypeface())

    fun createTag(value: String): CharacterStyle =
        TagSpan(value, specialSpansColors)

    fun createMention(value: String): CharacterStyle =
        MentionSpan(
            value,
            specialSpansColors
        )

        fun createLink(value: LinkInfo): CharacterStyle =
            LinkSpan(
                value,
                specialSpansColors
            )
}