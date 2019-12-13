package io.golos.domain.use_cases.posts

import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.PostsConfigurationDomain

interface GetPostsUseCase {
    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain>
}