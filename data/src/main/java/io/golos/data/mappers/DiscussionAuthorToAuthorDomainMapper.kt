package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.PostDomain


class DiscussionAuthorToAuthorDomainMapper : Function1<DiscussionAuthor, PostDomain.AuthorDomain> {

    override fun invoke(discussionAuthor: DiscussionAuthor): PostDomain.AuthorDomain {
        return PostDomain.AuthorDomain(discussionAuthor.avatarUrl,
            discussionAuthor.userId.name,
            discussionAuthor.username)
    }
}