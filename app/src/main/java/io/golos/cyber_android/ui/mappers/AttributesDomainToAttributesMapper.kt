package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class AttributesDomainToAttributesMapper : Function1<PostDomain.DocumentDomain.AttributesDomain, Post.Document.Attributes> {

    override fun invoke(attributesDomain: PostDomain.DocumentDomain.AttributesDomain): Post.Document.Attributes {

        return Post.Document.Attributes(attributesDomain.type,
            attributesDomain.version)
    }
}