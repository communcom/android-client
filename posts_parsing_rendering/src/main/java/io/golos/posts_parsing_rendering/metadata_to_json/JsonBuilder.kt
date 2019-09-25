package io.golos.posts_parsing_rendering.metadata_to_json

import io.golos.posts_parsing_rendering.BlockType

interface JsonBuilderBlocks {
    fun putBlock(type: BlockType, isLast: Boolean, contentAction: (JsonBuilderBlocks) -> Unit)

    fun putBlock(type: BlockType, isLast: Boolean, content: String)

    fun putBlock(
        type: BlockType,
        vararg attributes: PostAttribute,
        isLast: Boolean,
        contentAction: (JsonBuilderBlocks) -> Unit)

    fun putBlock(
        type: BlockType,
        vararg attributes: PostAttribute,
        isLast: Boolean,
        content: String)
}

interface JsonBuilderItems {
    fun putItem(key: String, value: String, isLast: Boolean = false)

    fun putItem(key: String, value: Long, isLast: Boolean = false)

    fun putItem(key: String, value: Array<out String>, isLast: Boolean = false)
}

interface JsonBuilder: JsonBuilderBlocks {
    fun build(): String

    fun clear()
}