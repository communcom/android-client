package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class ContentBodyDomainListToContentBodyListMapper :
    Function1<List<PostDomain.DocumentDomain.ContentDomain.ContentBodyDomain>, List<Post.Document.Content.ContentBody>> {

    override fun invoke(contentBodyDomainList: List<PostDomain.DocumentDomain.ContentDomain.ContentBodyDomain>): List<Post.Document.Content.ContentBody> {
        return contentBodyDomainList.map {
            ContentBodyDomainToContentBodyMapper().invoke(it)
        }
    }
}