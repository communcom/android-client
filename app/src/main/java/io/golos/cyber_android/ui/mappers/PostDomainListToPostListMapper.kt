package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class PostDomainListToPostListMapper : Function1<List<PostDomain>, List<Post>> {

    override fun invoke(postDomainList: List<PostDomain>): List<Post> {
        return postDomainList
            .map { PostDomainToPostMapper().invoke(it) }
    }
}