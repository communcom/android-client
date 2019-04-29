package io.golos.cyber_android.locator

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.model.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
interface RepositoriesHolder {
    val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>
    val authRepository: Repository<AuthState, AuthRequest>
    val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
    val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>
    val embedsRepository: Repository<ProcessedLinksEntity, EmbedRequest>
    val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>
    val countriesRepository: Repository<CountriesList, CountriesRequest>
    val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>
    val imageUploadRepository: Repository<UploadedImagesEntity, ImageUploadRequest>
}