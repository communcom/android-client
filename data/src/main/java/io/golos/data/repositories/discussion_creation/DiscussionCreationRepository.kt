package io.golos.data.repositories.discussion_creation

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity

interface DiscussionCreationRepository {
    suspend fun createOrUpdate(params: DiscussionCreationRequestEntity): Either<DiscussionCreationResultEntity, Throwable>
}