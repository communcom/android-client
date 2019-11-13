package io.golos.cyber_android.ui.dto.document

data class Video(
    override val id: Int,
    override val content: String,
    override val type: String,
    val attribute: Attribute? = null
) : Document {
    data class Attribute(
        val title: String? = null,
        val providerName: String? = null,
        val author: String? = null,
        val authorUrl: String? = null,
        val description: String? = null,
        val thumbnailUrl: String? = null,
        val thumbnailSize: String? = null,
        val html: String? = null
    )
}