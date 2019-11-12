package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain


class AuthorDomainToAuthorMapper : Function1<PostDomain.AuthorDomain, Post.Author> {

    override fun invoke(authorDomain: PostDomain.AuthorDomain): Post.Author {
        return Post.Author(authorDomain.avatarUrl,
            authorDomain.userId,
            authorDomain.username)
    }
}