package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileCommentsModelImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider
): ProfileCommentsModel, ModelBaseImpl() {

    override suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            //discussionRepository.getComments()
            listOf<CommentDomain>()
        }
    }

    override suspend fun upVote(commentId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            // todo try to call upVote from repository
        }
    }

    override suspend fun downVote(commentId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            // todo try to call downVote from repository
        }
    }
}