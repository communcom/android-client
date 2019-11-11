package io.golos.domain.interactors.posts

import io.golos.domain.entities.PostDomain
import io.golos.domain.entities.PostsConfigurationDomain
import javax.inject.Inject

class GetPostsUseCaseImpl @Inject constructor(private val postsRepository: PostsRepository): GetPostsUseCase {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain> = postsRepository.getPosts(postsConfigurationDomain)
}