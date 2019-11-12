package io.golos.cyber_android.ui.mappers

import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class AttributesDomainToAttributesMapper : Function1<DocumentEntity.AttributesEntity, PostDomain.DocumentDomain.AttributesDomain> {

    override fun invoke(attributesEntity: DocumentEntity.AttributesEntity): PostDomain.DocumentDomain.AttributesDomain {

        return PostDomain.DocumentDomain.AttributesDomain(attributesEntity.type,
            attributesEntity.version)
    }
}