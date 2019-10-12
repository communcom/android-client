package io.golos.data.repositories.discussion

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity

interface DiscussionRepository {
    suspend fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity

    suspend fun getPost(user: CyberName, permlink: Permlink): PostModel
}