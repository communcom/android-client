package io.golos.cyber_android.application.dependency_injection

import dagger.Binds
import dagger.Module
import io.golos.abi.implementation.publish.CreatemssgPublishStruct
import io.golos.abi.implementation.publish.DeletemssgPublishStruct
import io.golos.abi.implementation.publish.UpdatemssgPublishStruct
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorFingerprint
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.cyber_android.core.key_value_storage.storages.StorageOperationsInstance
import io.golos.cyber_android.core.key_value_storage.storages.combined.CombinedStorage
import io.golos.cyber_android.core.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.cyber_android.core.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.cyber_android.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.utils.HtmlToSpannableTransformerImpl
import io.golos.data.api.*
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import javax.inject.Named

@Suppress("unused")
@Module
abstract class AppModuleBinds {
    //region Key-value storage
    @Binds
    abstract fun provideKeyValueStorageFacade(facade: KeyValueStorageFacadeImpl): KeyValueStorageFacade

    @Binds
    abstract fun provideKeyValueStorage(storage: CombinedStorage): Storage

    @Binds
    @ApplicationScope
    @Named(Clarification.CACHE)
    abstract fun provideCacheStorage(storage: InMemoryStorage): StorageOperationsInstance

    @Binds
    @Named(Clarification.PERSISTENT)
    abstract fun providePersistentStorage(storage: SharedPreferencesStorage): StorageOperationsInstance
    // endregion

    // region Encryption
    @Binds
    @Named(Clarification.RSA)
    abstract fun provideEncryptor(encryptor: EncryptorRSA): Encryptor

    @Binds
    abstract fun provideEncryptorForFingerprint(encryptor: EncryptorAES): EncryptorFingerprint
    // endregion

    @Binds
    abstract fun provideStringsConverter(converter: StringsConverterImpl): StringsConverter

    // region Mappers
    @Binds
    abstract fun provideCyberPostToEntityMapper(mapper: CyberPostToEntityMapper): CyberPostToEntityMapper

    @Binds
    abstract fun provideVoteToEntityMapper(mapper: VoteRequestModelToEntityMapper): VoteRequestModelToEntityMapper

    @Binds
    abstract fun provideCyberFeedToEntityMapper(mapper: CyberFeedToEntityMapper): CyberFeedToEntityMapper

    @Binds
    @ApplicationScope
    abstract fun providePostEntityToModelMapper(mapper: PostEntityEntitiesToModelMapper): EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel>

    @Binds
    abstract fun provideFeedEntityToModelMapper(mapper: PostFeedEntityToModelMapper): EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>>

    @Binds
    @ApplicationScope
    abstract fun provideVoteEntityToPostMapper(mapper: VoteRequestEntityToModelMapper): EntityToModelMapper<VoteRequestEntity, VoteRequestModel>

    @Binds
    @ApplicationScope
    abstract fun provideCommentEntityToModelMapper(mapper: CommentEntityToModelMapper): EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel>

    @Binds
    abstract fun provideCommentFeeEntityToModelMapper(mapper: CommentsFeedEntityToModelMapper): EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>>

    @Binds
    @ApplicationScope
    abstract fun provideToCountriesModelMapper(mapper: CountryEntityToModelMapper): EntityToModelMapper<CountryEntity, CountryModel>

    @Binds
    abstract fun provideToRegistrationMapper(mapper: UserRegistrationStateEntityMapper): CyberToEntityMapper<UserRegistrationStateRelatedData, UserRegistrationStateEntity>

    @Binds
    abstract fun provideIfremlyEmbedMapper(mapper: IfremlyEmbedMapper): CyberToEntityMapper<IFramelyEmbedResultRelatedData, LinkEmbedResult>

    @Binds
    abstract fun provideOembedMapper(mapper: OembedMapper): CyberToEntityMapper<OembedResultRelatedData, LinkEmbedResult>

    @Binds
    abstract fun provideCyberCommentToEntityMapper(mapper: CyberCommentToEntityMapper): CyberToEntityMapper<CyberDiscussion, CommentEntity>

    @Binds
    abstract fun providecyberCommentFeedToEntityMapper(mapper: CyberCommentsToEntityMapper): CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>>

    @Binds
    abstract fun provideDiscussionCreateResultToEntityMapper (mapper: DiscussionCreateResultToEntityMapper): CyberToEntityMapper<CreatemssgPublishStruct, DiscussionCreationResultEntity>

    @Binds
    abstract fun provideDiscussionUpdateResultToEntityMapper(mapper: DiscussionUpdateResultToEntityMapper): CyberToEntityMapper<UpdatemssgPublishStruct, UpdatePostResultEntity>

    @Binds
    abstract fun provideDiscussionDeleteResultToEntityMapper(mapper: DiscussionDeleteResultToEntityMapper): CyberToEntityMapper<DeletemssgPublishStruct, DeleteDiscussionResultEntity>

    @Binds
    abstract fun provideRequestEntityToArgumentsMapper(mapper: RequestEntityToArgumentsMapper): EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>

    @Binds
    abstract fun provideCyberToAppErrorMapperImpl(mapper: CyberToAppErrorMapperImpl): CyberToAppErrorMapper
    // endregion

    // region Cyber4jApiService
    @Binds
    @ApplicationScope
    abstract fun providePostsApiService(service: Cyber4jApiService): PostsApiService

    @Binds
    @ApplicationScope
    abstract fun provideAuthApi(service: Cyber4jApiService): AuthApi

    @Binds
    @ApplicationScope
    abstract fun provideVoteApi(service: Cyber4jApiService): VoteApi

    @Binds
    @ApplicationScope
    abstract fun provideCommentsApiService(service: Cyber4jApiService): CommentsApiService

    @Binds
    @ApplicationScope
    abstract fun provideEmbedApi(service: Cyber4jApiService): EmbedApi

    @Binds
    @ApplicationScope
    abstract fun provideDiscussionsCreationApi(service: Cyber4jApiService): DiscussionsCreationApi

    @Binds
    @ApplicationScope
    abstract fun provideRegistrationApi(service: Cyber4jApiService): RegistrationApi

    @Binds
    @ApplicationScope
    abstract fun provideSettingsApi(service: Cyber4jApiService): SettingsApi

    @Binds
    @ApplicationScope
    abstract fun provideImageUploadApi(service: Cyber4jApiService): ImageUploadApi

    @Binds
    @ApplicationScope
    abstract fun provideEventsApi(service: Cyber4jApiService): EventsApi

    @Binds
    @ApplicationScope
    abstract fun provideUserMetadataApi(service: Cyber4jApiService): UserMetadataApi

    @Binds
    @ApplicationScope
    abstract fun provideTransactionsApi(service: Cyber4jApiService): TransactionsApi

    @Binds
    @ApplicationScope
    abstract fun providePushNotificationsApi(service: Cyber4jApiService): PushNotificationsApi
    // endregion

    // region Transformers
    @Binds
    abstract fun provideFromHtmlTransformer(transformer: HtmlToSpannableTransformerImpl): HtmlToSpannableTransformer

    @Binds
    abstract fun provideFromSpannableToHtml(transformer: FromSpannedToHtmlTransformerImpl): FromSpannedToHtmlTransformer
    // endregion

    @Binds
    abstract fun provideDeviceIdProvider(provider: MyDeviceIdProvider): DeviceIdProvider

    //region Approvers
    @Binds
    abstract fun provideFeedUpdateApprover(approver: FeedUpdateApprover): RequestApprover<PostFeedUpdateRequest>

    @Binds
    abstract fun provideCommentApprover(approver: CommentUpdateApprover): RequestApprover<CommentFeedUpdateRequest>
    // endregion
}