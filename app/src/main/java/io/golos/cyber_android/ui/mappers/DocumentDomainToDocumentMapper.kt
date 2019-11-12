package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
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