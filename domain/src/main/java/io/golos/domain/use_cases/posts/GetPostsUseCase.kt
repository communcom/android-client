package io.golos.domain.use_cases.posts

import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.TypeObjectDomain

interface GetPostsUseCase {
    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain, typeObject: TypeObjectDomain): List<PostDomain>
}