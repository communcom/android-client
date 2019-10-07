package io.golos.domain.posts_parsing_rendering.json_builder

import io.golos.domain.posts_parsing_rendering.Attribute

/**
 * One item of an Attribute block
 */
open class PostAttribute {
    private interface PostAttributeWriter {
        fun toJson(builder: JsonBuilderItems, isLast: Boolean)
    }

    private class StringPostAttribute(private val key: Attribute, private val value: String):
        PostAttributeWriter {
        override fun toJson(builder: JsonBuilderItems, isLast: Boolean) = builder.putItem(key.value, value, isLast)
    }

    private class LongPostAttribute(private val key: Attribute, private val value: Long):
        PostAttributeWriter {
        override fun toJson(builder: JsonBuilderItems, isLast: Boolean) = builder.putItem(key.value, value, isLast)
    }

    private class StringArrayPostAttribute(private val key: Attribute, private val value: Array<out String>):
        PostAttributeWriter {
        override fun toJson(builder: JsonBuilderItems, isLast: Boolean) = builder.putItem(key.value, value, isLast)
    }

    private val attribute: PostAttributeWriter

    constructor(key: Attribute, value: String) {
        attribute = StringPostAttribute(key, value)
    }

    constructor(key: Attribute, value: Long) {
        attribute = LongPostAttribute(key, value)
    }

    constructor(key: Attribute, vararg value : String) {
        attribute = StringArrayPostAttribute(key, value)
    }

    fun toJson(builder: JsonBuilderItems, isLast: Boolean) = attribute.toJson(builder, isLast)
}