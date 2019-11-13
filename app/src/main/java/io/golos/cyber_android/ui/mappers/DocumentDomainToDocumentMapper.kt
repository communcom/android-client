package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.document.Document
import io.golos.cyber_android.ui.dto.document.DocumentType
import io.golos.domain.dto.PostDomain

class DocumentDomainToDocumentMapper : Function1<PostDomain.DocumentDomain?, Post.Document?> {

    override fun invoke(documentDomain: PostDomain.DocumentDomain?): Post.Document? {

        return documentDomain?.let {
            Post.Document(
                AttributesDomainToAttributesMapper().invoke(it.attributes),
                ContentDomainListToContentListMapper().invoke(it.content),
                it.id,
                it.type
            )
        }
    }
}

//todo new parser for content
class DocumentDomainToPostDocumentMapper : Function1<PostDomain.DocumentDomain?, List<Document>> {

    override fun invoke(documentDomain: PostDomain.DocumentDomain?): List<Document> {
        val documents = mutableListOf<Document>()
        documentDomain?.let { document ->
            when (DocumentType.getDocumentTypeByType(document.type)) {
                DocumentType.PARAGRAPH -> { //todo move to mapper
                    val documentContent = ContentDomainListToContentListMapper().invoke(
                        document.content
                    )
                    documentContent.forEach { content ->
                        // parse to paragraph dto
                    }
                }
                DocumentType.IMAGE -> TODO("move to mapper")
                DocumentType.VIDEO -> TODO("move to mapper")
                DocumentType.WEB_SITE -> TODO("move to mapper")
                DocumentType.ATTACHMENTS -> TODO("move to mapper")
            }
        }
        return documents
    }
}