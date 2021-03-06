package io.golos.domain.use_cases.posts

import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.dto.TypeObjectDomain
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPostsUseCaseImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider
) : GetPostsUseCase {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain, typeObject: TypeObjectDomain): List<PostDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.getPosts(postsConfigurationDomain, typeObject)
        }
    }
}