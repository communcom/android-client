package io.golos.cyber_android.ui.dto.document

data class Image(
    override val id: Int,
    override val content: String,
    override val type: String,
    val attribute: Attribute? = null
) : Document {
    data class Attribute(val description: String)
}