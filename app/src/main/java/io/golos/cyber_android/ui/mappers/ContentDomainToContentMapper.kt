package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class ContentDomainToContentMapper : Function1<PostDomain.DocumentDomain.ContentDomain, Post.Document.Content> {

    override fun invoke(contentDomain: PostDomain.DocumentDomain.ContentDomain): Post.Document.Content {

        return Post.Document.Content(
            ContentBodyDomainListToContentBodyListMapper().invoke(contentDomain.contentBodyList),
            contentDomain.id,
            contentDomain.type
        )
    }
}