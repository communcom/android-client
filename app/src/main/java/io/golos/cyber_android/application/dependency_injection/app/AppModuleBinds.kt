package io.golos.cyber_android.application.dependency_injection.app

import dagger.Binds
import dagger.Module
import io.golos.abi.implementation.publish.CreatemssgPublishStruct
import io.golos.abi.implementation.publish.DeletemssgPublishStruct
import io.golos.abi.implementation.publish.UpdatemssgPublishStruct
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber_android.application.logger.LoggerImpl
import io.golos.cyber_android.core.backup.facade.BackupKeysFacadeImpl
import io.golos.cyber_android.core.backup.facade.BackupKeysFacadeSync
import io.golos.domain.DeviceInfoService
import io.golos.cyber_android.core.device_info.DeviceInfoServiceImpl
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorFingerprint
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManagerImpl
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.cyber_android.core.key_value_storage.storages.StorageOperationsInstance
import io.golos.cyber_android.core.key_value_storage.storages.combined.CombinedStorage
import io.golos.cyber_android.core.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.cyber_android.core.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.cyber_android.core.resources.AppResourcesProviderImpl
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.cyber_android.core.user_keys_store.UserKeyStoreImpl
import io.golos.cyber_android.ui.screens.login.signin.qr_code.keys_extractor.QrCodeKeysExtractor
import io.golos.cyber_android.ui.screens.login.signin.qr_code.keys_extractor.QrCodeKeysExtractorImpl
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractorImpl
import io.golos.cyber_android.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.utils.HtmlToSpannableTransformerImpl
import io.golos.data.api.*
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.data.repositories.*
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
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

    //region Approversv
    @Binds
    abstract fun provideFeedUpdateApprover(approver: FeedUpdateApprover): RequestApprover<PostFeedUpdateRequest>

    @Binds
    abstract fun provideCommentApprover(approver: CommentUpdateApprover): RequestApprover<CommentFeedUpdateRequest>
    // endregion

    @Binds
    abstract fun provideUserKeyStore(store: UserKeyStoreImpl): UserKeyStore

    //region Approversv
    @Binds
    abstract fun providePostMerger(merger: PostMerger): EntityMerger<PostEntity>

    @Binds
    abstract fun provideFeedMerger(merger: PostFeedMerger): EntityMerger<FeedRelatedData<PostEntity>>

    @Binds
    abstract fun provideEmptyPostFeedProducer(merger: EmptyPostFeedProducer): EmptyEntityProducer<FeedEntity<PostEntity>>

    @Binds
    abstract fun provideCommentMerger(merger: CommentMerger): EntityMerger<CommentEntity>

    @Binds
    abstract fun provideCommentFeedMerger(merger: CommentFeedMerger): EntityMerger<FeedRelatedData<CommentEntity>>

    @Binds
    abstract fun provideEmptyCommentFeedProducer(merger: EmptyCommentFeedProducer): EmptyEntityProducer<FeedEntity<CommentEntity>>
    // endregion

    @ApplicationScope
    @Binds
    abstract fun provideLogger(logger: LoggerImpl): Logger

    @Binds
    abstract fun provideFingerprintAuthManager(manager: FingerprintAuthManagerImpl): FingerprintAuthManager

    // region Repositories
    @Binds
    @ApplicationScope
    abstract fun providePostFeedRepository(repository: PostsFeedRepository): AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideCommentsRepository(repository: CommentsFeedRepository): DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideDiscussionCreationRepository(repository: DiscussionCreationRepository): Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>

    @Binds
    @ApplicationScope
    abstract fun provideEmbedsRepository(repository: EmbedsRepository): Repository<ProcessedLinksEntity, EmbedRequest>

    @Binds
    @ApplicationScope
    abstract fun provideAuthRepository(repository: AuthStateRepository): Repository<AuthState, AuthRequest>

    @Binds
    @ApplicationScope
    abstract fun provideVoteRepository(repository: VoteRepository): Repository<VoteRequestEntity, VoteRequestEntity>

    @Binds
    @ApplicationScope
    abstract fun provideRegistrationRepository(repository: RegistrationRepository): Repository<UserRegistrationStateEntity, RegistrationStepRequest>

    @Binds
    abstract fun provideSettingsRepository(repository: SettingsRepository): Repository<UserSettingEntity, SettingChangeRequest>

    @Binds
    abstract fun provideImageUploadRepository(repository: ImageUploadRepository): Repository<UploadedImagesEntity, ImageUploadRequest>

    @Binds
    abstract fun provideEventsRepository(repository: EventsRepository): Repository<EventsListEntity, EventsFeedUpdateRequest>

    @Binds
    abstract fun provideUserMetadataRepository(repository: UserMetadataRepository): Repository<UserMetadataCollectionEntity, UserMetadataRequest>

    @Binds
    abstract fun providePushesRepository(repository: PushNotificationsRepository): Repository<PushNotificationsStateEntity, PushNotificationsStateUpdateRequest>
    // endregion

    // ------------- Sign In -----------
    @Binds
    abstract fun provideMasterPassKeysExtractor(extractor: MasterPassKeysExtractorImpl): MasterPassKeysExtractor

    @Binds
    abstract fun provideQrCodeKeysExtractor(extractor: QrCodeKeysExtractorImpl): QrCodeKeysExtractor
    // ------------ Sign In ------------

    @Binds
    abstract fun provideBackupKeysFacadeSync(facade: BackupKeysFacadeImpl): BackupKeysFacadeSync

    @Binds
    abstract fun provideDeviceInfoService(service: DeviceInfoServiceImpl): DeviceInfoService

    @Binds
    abstract fun provideAppResourcesProvider(provider: AppResourcesProviderImpl): AppResourcesProvider
}