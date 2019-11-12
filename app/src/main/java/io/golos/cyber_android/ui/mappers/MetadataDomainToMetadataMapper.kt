package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class MetadataDomainToMetadataMapper : Function1<PostDomain.MetaDomain, Post.Meta> {

    override fun invoke(metaDomain: PostDomain.MetaDomain): Post.Meta {
        return Post.Meta(metaDomain.creationTime)
    }

}