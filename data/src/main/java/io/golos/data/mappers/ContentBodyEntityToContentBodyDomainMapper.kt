package io.golos.data.mappers

import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class ContentBodyEntityToContentBodyDomainMapper : Function1<DocumentEntity.ContentEntity.ContentBodyEntity, PostDomain.DocumentDomain.ContentDomain.ContentBody> {

    override fun invoke(contentBodyEntity: DocumentEntity.ContentEntity.ContentBodyEntity): PostDomain.DocumentDomain.ContentDomain.ContentBody {

        return PostDomain.DocumentDomain.ContentDomain.ContentBody(contentBodyEntity.content,
            contentBodyEntity.id,
            contentBodyEntity.type)
    }
}