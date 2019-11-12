package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class ContentDomainListToContentListMapper : Function1<List<PostDomain.DocumentDomain.ContentDomain>, List<Post.Document.Content>> {

    override fun invoke(contentEntityList: List<PostDomain.DocumentDomain.ContentDomain>):List<Post.Document.Content> {
        return contentEntityList.map {
            ContentDomainToContentMapper().invoke(it)
        }
    }
}