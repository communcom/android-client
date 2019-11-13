package io.golos.cyber_android.ui.dto.document

sealed class Paragraph : Document {

    data class Text(
        override val id: Int,
        override val content: String,
        override val type: String,
        val attribute: Attribute? = null
    ) : Paragraph() {
        data class Attribute(
            val style: List<String>,
            val textColor: String
        )
    }

    data class Link(
        override val id: Int,
        override val content: String,
        override val type: String,
        val attribute: Attribute
    ) : Paragraph() {
        data class Attribute(val url: String)
    }

    data class Mention(
        override val id: Int,
        override val content: String,
        override val type: String
    ) : Paragraph()

    data class Tag(
        override val id: Int,
        override val content: String,
        override val type: String
    ) : Paragraph()

    enum class Type {
        TEXT,
        LINK,
        MENTION,
        TAG;

        companion object {
            fun getTypeByString(type: String): Type = valueOf(type.toUpperCase())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Paragraph

        if (id != other.id) return false
        if (content != other.content) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + content.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}