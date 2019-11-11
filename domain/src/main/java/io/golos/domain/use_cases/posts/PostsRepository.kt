package io.golos.domain.interactors.posts

import io.golos.domain.entities.PostDomain
import io.golos.domain.entities.PostsConfigurationDomain

interface PostsRepository {

    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain>
}