package io.golos.cyber_android.ui.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.dto.PostDomain


class PostDomainToPostMapper : Function1<CyberDiscussionRaw, PostDomain> {

    override fun invoke(discussionRaw: CyberDiscussionRaw): PostDomain {
        return PostDomain(
            DiscussionAuthorToAuthorDomainMapper().invoke(discussionRaw.author),
            CyberCommunityToCommunityDomainMapper().invoke(discussionRaw.community),
            ContentIdDomainToContentIdMapper().invoke(discussionRaw.contentId),
            DocumentDomainToDocumentMapper().invoke(discussionRaw.document),
            MetadataDomainToMetadataMapper().invoke(discussionRaw.meta),
            null,
            null,
            VotesDomainToVotesMapper().invoke(discussionRaw.votes)
        )
    }
}