package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class ContentBodyDomainToContentBodyMapper : Function1<PostDomain.DocumentDomain.ContentDomain.ContentBodyDomain, Post.Document.Content.ContentBody> {

    override fun invoke(contentBodyDomain: PostDomain.DocumentDomain.ContentDomain.ContentBodyDomain): Post.Document.Content.ContentBody {

        return Post.Document.Content.ContentBody(contentBodyDomain.content,
            contentBodyDomain.id,
            contentBodyDomain.type)
    }
}