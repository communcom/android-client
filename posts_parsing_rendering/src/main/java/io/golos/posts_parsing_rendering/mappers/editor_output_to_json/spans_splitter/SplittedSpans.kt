package io.golos.posts_parsing_rendering.mappers.editor_output_to_json.spans_splitter

import android.net.Uri

sealed class SplittedItem

data class SplittedText(val content: String): SplittedItem()

data class SplittedTag(val content: String): SplittedItem()

data class SplittedMention(val content: String): SplittedItem()

data class SplittedLink(val content: String, val uri: Uri): SplittedItem()
