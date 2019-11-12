package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain


class PostDomainToPostMapper : Function1<PostDomain, Post> {

    override fun invoke(postDomain: PostDomain): Post {
        return Post(
            AuthorDomainToAuthorMapper().invoke(postDomain.author),
            CommunityDomainToCommunityMapper().invoke(postDomain.community),
            ContentIdDomainToContentIdMapper().invoke(postDomain.contentId),
            DocumentDomainToDocumentMapper().invoke(postDomain.document),
            MetadataDomainToMetadataMapper().invoke(postDomain.meta),
            null,
            null,
            VotesDomainToVotesMapper().invoke(postDomain.votes)
        )
    }
}