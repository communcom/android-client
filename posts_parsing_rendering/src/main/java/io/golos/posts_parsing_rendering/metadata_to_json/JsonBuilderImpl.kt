package io.golos.posts_parsing_rendering.metadata_to_json

import io.golos.posts_parsing_rendering.BlockType

class JsonBuilderImpl private constructor(): JsonBuilder, JsonBuilderItems {
    companion object {
        fun create(): JsonBuilderBlocks = JsonBuilderImpl()
    }

    private val output = StringBuilder()

    private var idCounter: Long = 1

    override fun build(): String = output.toString()

    override fun clear() {
        output.clear()
    }

    @Suppress("RemoveRedundantSpreadOperator")
    override fun putBlock(type: BlockType, isLast: Boolean, contentAction: (JsonBuilderBlocks) -> Unit) =
        putBlock(type, *arrayOf(), isLast = isLast, contentAction = contentAction)

    @Suppress("RemoveRedundantSpreadOperator")
    override fun putBlock(type: BlockType, isLast: Boolean, content: String) =
        putBlock(type, *arrayOf(), isLast = isLast, content = content)

    override fun putBlock(
        type: BlockType,
        vararg attributes: PostAttribute,
        isLast: Boolean,
        contentAction: (JsonBuilderBlocks) -> Unit) =
            putBlock(type, attributes, isLast) {
                putItem("content", attributes.isEmpty()) {
                    output.append("[")
                    contentAction(this)
                    output.append("]")
                }
            }

    override fun putBlock(
        type: BlockType,
        vararg attributes: PostAttribute,
        isLast: Boolean,
        content: String) =
            putBlock(type, attributes, isLast) {
                putItem("content", content, attributes.isEmpty())
            }

    private fun putBlock(
        type: BlockType,
        attributes: Array<out PostAttribute>,
        isLast: Boolean,
        contentAction: () -> Unit) {

        output.append("{ ")

        putItem("id", getNextId())

        putItem("type", type.value)

        contentAction()

        if(attributes.isNotEmpty()) {
            putItem("attributes", true) {
                output.append("{ ")

                val attributesIndexes = attributes.indices
                for(i in attributesIndexes) {
                    attributes[i].toJson(this, i == attributesIndexes.last)
                }

                output.append(" }")
            }
        }

        output.append(" }")

        if(!isLast) {
            output.append(",")
        }
    }

    override fun putItem(key: String, value: String, isLast: Boolean) = putItem(key, isLast) { output.appendJsonString(value) }

    override fun putItem(key: String, value: Long, isLast: Boolean) = putItem(key, isLast) { output.append(value) }

    override fun putItem(key: String, value: Array<out String>, isLast: Boolean) = putItem(key, isLast) {
        output.append("[")
        output.append(value.joinToString { "\"$it\"" })
        output.append("]")
    }

    private fun putItem(key: String, isLast: Boolean, appendValueAction: () -> Unit) {
        output.appendJsonString(key)
        output.append(": ")
        appendValueAction()

        if(!isLast) {
            output.append(",")
        }
    }

    private fun getNextId() = idCounter++

    private fun StringBuilder.appendJsonString(s: String) {
        append("\'")
        append(s)
        append("\"")
    }
}