package io.golos.cyber_android.application.locator

import io.golos.data.repositories.countries.CountriesRepository
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.*

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
    val countriesRepository: CountriesRepository
    val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>
    val settingsRepository: Repository<UserSettingEntity, SettingChangeRequest>
    val imageUploadRepository: Repository<UploadedImagesEntity, ImageUploadRequest>
    val eventsRepository: Repository<EventsListEntity, EventsFeedUpdateRequest>
    val userMetadataRepository: Repository<UserMetadataCollectionEntity, UserMetadataRequest>
    val pushesRepository: Repository<PushNotificationsStateEntity, PushNotificationsStateUpdateRequest>
}