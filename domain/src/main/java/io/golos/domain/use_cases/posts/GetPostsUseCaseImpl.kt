package io.golos.domain.use_cases.posts

import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.repositories.DiscussionRepository
import javax.inject.Inject

class GetPostsUseCaseImpl @Inject constructor(private val postsRepository: DiscussionRepository): GetPostsUseCase {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain> = postsRepository.getPosts(postsConfigurationDomain)
}