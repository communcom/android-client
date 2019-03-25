package io.golos.cyber_android.locator

import io.golos.data.repositories.AbstractDiscussionsRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.PostFeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
interface RepositoriesHolder {
    val postFeedRepository: AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>
    val authRepository: Repository<AuthState, AuthRequest>
    val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
}