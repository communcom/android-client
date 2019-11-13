package io.golos.cyber_android.ui.dto.document

enum class DocumentType {

    PARAGRAPH,
    IMAGE,
    VIDEO,
    WEB_SITE,
    ATTACHMENTS;

    companion object {
        fun getDocumentTypeByType(type: String): DocumentType = valueOf(type.toUpperCase())
    }

}