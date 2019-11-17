package io.golos.posts_editor.utilities.post.spans

import android.graphics.Color
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.use_cases.post.editor_output.LinkInfo
import io.golos.domain.use_cases.post.toTypeface
import io.golos.posts_editor.components.input.spans.custom.LinkSpan
import io.golos.posts_editor.components.input.spans.custom.MentionSpan
import io.golos.posts_editor.components.input.spans.custom.TagSpan

object PostSpansFactory {
    @ColorInt
    private val specialSpansColors = Color.BLUE

    fun createTextColor(@ColorInt color: Int): CharacterStyle = ForegroundColorSpan(color)

    fun createTextStyle(style: TextStyle): CharacterStyle = StyleSpan(style.toTypeface())

    fun createTag(value: String): CharacterStyle = TagSpan(value,
        specialSpansColors
    )

    fun createMention(value: String): CharacterStyle = MentionSpan(value,
        specialSpansColors
    )

    fun createLink(value: LinkInfo): CharacterStyle = LinkSpan(value,
        specialSpansColors
    )
}