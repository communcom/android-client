package io.golos.cyber_android.application.dependency_injection.graph.app

import dagger.Binds
import dagger.Module
import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.abi.implementation.gls.publish.CreatemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.DeletemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.UpdatemssgGlsPublishStruct
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.services.model.MobileShowSettings
import io.golos.cyber4j.services.model.UserMetadataResult
import io.golos.cyber4j.services.model.UserSettings
import io.golos.cyber_android.application.AppCore
import io.golos.cyber_android.application.AppCoreImpl
import io.golos.cyber_android.application.dependency_injection.wrappers.Cyber4JDagger
import io.golos.cyber_android.application.dependency_injection.wrappers.Cyber4jApiServiceFakePosts
import io.golos.cyber_android.core.crashlytics.CrashlyticsFacadeImpl
import io.golos.cyber_android.core.device_info.DeviceInfoProviderImpl
import io.golos.cyber_android.core.display_info.DisplayInfoProvider
import io.golos.cyber_android.core.display_info.DisplayInfoProviderImpl
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
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacadeImpl
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacadeSync
import io.golos.cyber_android.core.logger.LoggerImpl
import io.golos.cyber_android.core.resources.AppResourcesProviderImpl
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.cyber_android.core.ui_monitor.UIMonitor
import io.golos.cyber_android.core.ui_monitor.UIMonitorImpl
import io.golos.cyber_android.core.user_keys_store.UserKeyStoreImpl
import io.golos.cyber_android.services.fcm.FcmTokenProviderImpl
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.keys_extractor.QrCodeKeysExtractor
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.keys_extractor.QrCodeKeysExtractorImpl
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor.MasterPassKeysExtractorImpl
import io.golos.cyber_android.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.utils.HtmlToSpannableTransformerImpl
import io.golos.cyber_android.utils.ImageCompressorImpl
import io.golos.data.api.*
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.data.repositories.*
import io.golos.data.repositories.discussion_creation.DiscussionCreationRepository
import io.golos.data.repositories.discussion_creation.DiscussionCreationRepositoryImpl
import io.golos.data.repositories.discussion_creation.DiscussionCreationRepositoryLiveData
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryImpl
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryLiveData
import io.golos.data.utils.ImageCompressor
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import javax.inject.Named

@Suppress("unused")
@Module
abstract class AppModuleBinds {
    @Binds
    @ApplicationScope
    abstract fun provideCyber(cyber: Cyber4JDagger): Cyber4J

    @Binds
    @ApplicationScope
    abstract fun provideAppCore(appCore: AppCoreImpl): AppCore

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

    @Binds
    abstract fun providesMyDefaultSettingProvider(provider: MyDefaultSettingProvider): DefaultSettingProvider

    // region Mappers
    @Binds
    abstract fun provideUserMetadataToEntityMapper(mapper: UserMetadataToEntityMapper): CyberToEntityMapper<UserMetadataResult, UserMetadataEntity>

    @Binds
    abstract fun provideCyberPostToEntityMapper(mapper: CyberPostToEntityMapper): CyberToEntityMapper<CyberDiscussion, PostEntity>

    @Binds
    abstract fun provideVoteToEntityMapper(mapper: VoteRequestModelToEntityMapper): ModelToEntityMapper<VoteRequestModel, VoteRequestEntity>

    @Binds
    abstract fun provideCyberFeedToEntityMapper(mapper: CyberFeedToEntityMapper): CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>>

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
    abstract fun provideDiscussionCreateResultToEntityMapper (mapper: DiscussionCreateResultToEntityMapper): CyberToEntityMapper<CreatemssgGlsPublishStruct, DiscussionCreationResultEntity>

    @Binds
    abstract fun provideDiscussionUpdateResultToEntityMapper(mapper: DiscussionUpdateResultToEntityMapper): CyberToEntityMapper<UpdatemssgGlsPublishStruct, UpdatePostResultEntity>

    @Binds
    abstract fun provideDiscussionDeleteResultToEntityMapper(mapper: DiscussionDeleteResultToEntityMapper): CyberToEntityMapper<DeletemssgGlsPublishStruct, DeleteDiscussionResultEntity>

    @Binds
    abstract fun provideRequestEntityToArgumentsMapper(mapper: RequestEntityToArgumentsMapper): EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>

    @Binds
    abstract fun provideCyberToAppErrorMapperImpl(mapper: CyberToAppErrorMapperImpl): CyberToAppErrorMapper

    @Binds
    @ApplicationScope
    abstract fun provideEventsToEntityMapper(mapper: EventsToEntityMapper): CyberToEntityMapper<EventsListDataWithQuery, EventsListEntity>

    @Binds
    abstract fun provideEventsEntityMerger(merger: EventsEntityMerger): EntityMerger<EventsListEntity>

    @Binds
    abstract fun provideEventsApprover(approver: EventsApprover): RequestApprover<EventsFeedUpdateRequest>

    @Binds
    abstract fun provideSettingsToEntityMapper(mapper: SettingsToEntityMapper): CyberToEntityMapper<UserSettings, UserSettingEntity>

    @Binds
    abstract fun provideSettingToCyberMapper(mapper: SettingToCyberMapper): EntityToCyberMapper<NotificationSettingsEntity, MobileShowSettings>
    // endregion

    // region Cyber4jApiService
    @Binds
    @ApplicationScope
    abstract fun providePostsApiService(service: Cyber4jApiServiceFakePosts): PostsApiService

    @Binds
    @ApplicationScope
    abstract fun provideAuthApi(service: Cyber4jApiServiceFakePosts): AuthApi

    @Binds
    @ApplicationScope
    abstract fun provideVoteApi(service: Cyber4jApiServiceFakePosts): VoteApi

    @Binds
    @ApplicationScope
    abstract fun provideCommentsApiService(service: Cyber4jApiServiceFakePosts): CommentsApiService

    @Binds
    @ApplicationScope
    abstract fun provideEmbedApi(service: Cyber4jApiServiceFakePosts): EmbedApi

    @Binds
    @ApplicationScope
    abstract fun provideDiscussionsCreationApi(service: Cyber4jApiServiceFakePosts): DiscussionsCreationApi

    @Binds
    @ApplicationScope
    abstract fun provideRegistrationApi(service: Cyber4jApiServiceFakePosts): RegistrationApi

    @Binds
    @ApplicationScope
    abstract fun provideSettingsApi(service: Cyber4jApiServiceFakePosts): SettingsApi

    @Binds
    @ApplicationScope
    abstract fun provideImageUploadApi(service: Cyber4jApiServiceFakePosts): ImageUploadApi

    @Binds
    @ApplicationScope
    abstract fun provideEventsApi(service: Cyber4jApiServiceFakePosts): EventsApi

    @Binds
    @ApplicationScope
    abstract fun provideUserMetadataApi(service: Cyber4jApiServiceFakePosts): UserMetadataApi

    @Binds
    @ApplicationScope
    abstract fun provideTransactionsApi(service: Cyber4jApiServiceFakePosts): TransactionsApi

    @Binds
    @ApplicationScope
    abstract fun providePushNotificationsApi(service: Cyber4jApiServiceFakePosts): PushNotificationsApi
    // endregion

    // region Transformers
    @Binds
    abstract fun provideFromHtmlTransformer(transformer: HtmlToSpannableTransformerImpl): HtmlToSpannableTransformer

    @Binds
    abstract fun provideFromSpannableToHtml(transformer: FromSpannedToHtmlTransformerImpl): FromSpannedToHtmlTransformer
    // endregion

    @Binds
    abstract fun provideDeviceIdProvider(provider: MyDeviceIdProvider): DeviceIdProvider

    //region Approver
    @Binds
    abstract fun provideFeedUpdateApprover(approver: FeedUpdateApprover): RequestApprover<PostFeedUpdateRequest>

    @Binds
    abstract fun provideCommentApprover(approver: CommentUpdateApprover): RequestApprover<CommentFeedUpdateRequest>
    // endregion

    @Binds
    abstract fun provideUserKeyStore(store: UserKeyStoreImpl): UserKeyStore

    //region Producers
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

    @Binds
    @ApplicationScope
    abstract fun provideLogger(logger: LoggerImpl): Logger

    @Binds
    abstract fun provideFingerprintAuthManager(manager: FingerprintAuthManagerImpl): FingerprintAuthManager

    @Binds
    abstract fun provideImageCompressor(compressor: ImageCompressorImpl): ImageCompressor

    // region Repositories
    @Binds
    @ApplicationScope
    abstract fun providePostFeedRepository(repository: PostsFeedRepository): DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideCommentsRepository(repository: CommentsFeedRepository): DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideDiscussionCreationRepositoryLiveData(repository: DiscussionCreationRepositoryLiveData): Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>

    @Binds
    abstract fun provideDiscussionCreationRepository(repository: DiscussionCreationRepositoryImpl): DiscussionCreationRepository

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
    abstract fun provideImageUploadRepositoryLiveData(repository: ImageUploadRepositoryLiveData): Repository<UploadedImagesEntity, ImageUploadRequest>

    @Binds
    abstract fun provideImageUploadRepository(repository: ImageUploadRepositoryImpl): ImageUploadRepository

    @Binds
    abstract fun provideEventsRepository(repository: EventsRepository): Repository<EventsListEntity, EventsFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideUserMetadataRepository(repository: UserMetadataRepository): Repository<UserMetadataCollectionEntity, UserMetadataRequest>

    @Binds
    abstract fun provideFcmTokenProvider(provider: FcmTokenProviderImpl): FcmTokenProvider

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
    abstract fun provideDeviceInfoService(service: DeviceInfoProviderImpl): DeviceInfoProvider

    @Binds
    abstract fun provideAppResourcesProvider(provider: AppResourcesProviderImpl): AppResourcesProvider

    @Binds
    abstract fun provideDisplayInfoProvider(provider: DisplayInfoProviderImpl): DisplayInfoProvider

    @Binds
    @ApplicationScope
    abstract fun provideCrashlyticsFacade(facade: CrashlyticsFacadeImpl): CrashlyticsFacade

    @Binds
    @ApplicationScope
    abstract fun provideUIMonitor(monitor: UIMonitorImpl): UIMonitor
}