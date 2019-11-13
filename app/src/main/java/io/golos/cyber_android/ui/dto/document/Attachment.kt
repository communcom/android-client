package io.golos.cyber_android.ui.dto.document

data class Attachment(
    override val id: Int,
    override val type: String,
    override val content: String = "", //todo think about this param at the Document interface
    val attribute: List<Document>
) : Document