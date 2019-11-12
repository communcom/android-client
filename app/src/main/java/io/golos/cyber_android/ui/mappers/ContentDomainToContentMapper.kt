package io.golos.cyber_android.ui.mappers

import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class ContentDomainToContentMapper : Function1<DocumentEntity.ContentEntity, PostDomain.DocumentDomain.ContentDomain> {

    override fun invoke(contentEntity: DocumentEntity.ContentEntity): PostDomain.DocumentDomain.ContentDomain {

        return PostDomain.DocumentDomain.ContentDomain(
            ContentBodyEntityListToContentBodyDomainListMapper().invoke(contentEntity.contentBodyList),
            contentEntity.id,
            contentEntity.type)
    }
}