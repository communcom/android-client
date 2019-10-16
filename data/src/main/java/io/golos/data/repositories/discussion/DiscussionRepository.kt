package io.golos.data.repositories.discussion

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity

interface DiscussionRepository {
    fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity

    fun getPost(user: CyberName, permlink: Permlink): PostModel

    fun deletePost(postId: DiscussionIdModel)
}