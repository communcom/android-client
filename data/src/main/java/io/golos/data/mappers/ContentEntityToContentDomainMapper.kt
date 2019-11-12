package io.golos.data.mappers

import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class ContentEntityToContentDomainMapper : Function1<DocumentEntity.ContentEntity, PostDomain.DocumentDomain.ContentDomain> {

    override fun invoke(contentEntity: DocumentEntity.ContentEntity): PostDomain.DocumentDomain.ContentDomain {

        return PostDomain.DocumentDomain.ContentDomain(ContentBodyEntityListToContentBodyDomainListMapper().invoke(contentEntity.contentBodyEntityList),
            contentEntity.id,
            contentEntity.type)
    }
}