package io.golos.cyber_android.locator

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.EmbedRequest
import io.golos.domain.model.PostFeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
interface RepositoriesHolder {
    val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>
    val authRepository: Repository<AuthState, AuthRequest>
    val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
    val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>
    val embedsRepository : Repository<ProcessedLinksEntity, EmbedRequest>
}