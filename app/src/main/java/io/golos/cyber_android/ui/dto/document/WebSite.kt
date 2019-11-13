package io.golos.cyber_android.ui.dto.document

data class WebSite(
    override val id: Int,
    override val content: String,
    override val type: String,
    val attribute: Attribute? = null
) : Document {
    data class Attribute(
        val title: String? = null,
        val provider_name: String? = null,
        val description: String? = null,
        val thumbnail_url: String? = null
    )
}