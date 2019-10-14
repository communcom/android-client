package io.golos.cyber_android.ui.shared_fragments.post.model

import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.discussion.DiscussionRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import javax.inject.Inject

class PostPageModelImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val dispatchersProvider: DispatchersProvider,
    private val discussionRepository: DiscussionRepository
) : ModelBaseImpl(), PostPageModel {

    override suspend fun getPost(): PostModel =
        discussionRepository.getPost(postToProcess.userId.toCyberName(), postToProcess.permlink)
}