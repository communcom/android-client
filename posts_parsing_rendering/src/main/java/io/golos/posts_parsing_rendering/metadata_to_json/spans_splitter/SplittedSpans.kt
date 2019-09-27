package io.golos.posts_parsing_rendering.metadata_to_json.spans_splitter

import android.net.Uri
import io.golos.domain.post_editor.LinkType

sealed class SplittedItem

data class SplittedText(val content: String): SplittedItem()

data class SplittedTag(val content: String): SplittedItem()

data class SplittedMention(val content: String): SplittedItem()

data class SplittedLink(val content: String, val uri: Uri, val type: LinkType, val thumbnailUrl: Uri): SplittedItem()
