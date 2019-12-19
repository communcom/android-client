package io.golos.cyber_android.application.dependency_injection.graph.app

import dagger.Binds
import dagger.Module
import io.golos.commun4j.Commun4j
import io.golos.cyber_android.application.AppCore
import io.golos.cyber_android.application.AppCoreImpl
import io.golos.cyber_android.application.dependency_injection.wrappers.Cyber4JDagger
import io.golos.cyber_android.core.clipboard.ClipboardUtils
import io.golos.cyber_android.core.clipboard.ClipboardUtilsImpl
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
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.cyber_android.core.ui_monitor.UIMonitor
import io.golos.cyber_android.core.ui_monitor.UIMonitorImpl
import io.golos.cyber_android.core.user_keys_store.UserKeyStoreImpl
import io.golos.cyber_android.services.fcm.FcmTokenProviderImpl
import io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.keys_extractor.QrCodeKeysExtractor
import io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.keys_extractor.QrCodeKeysExtractorImpl
import io.golos.cyber_android.ui.screens.login_sign_in_old.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.ui.screens.login_sign_in_old.user_name.keys_extractor.MasterPassKeysExtractorImpl
import io.golos.cyber_android.ui.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.ui.utils.HtmlToSpannableTransformerImpl
import io.golos.cyber_android.ui.utils.ImageCompressorImpl
import io.golos.domain.api.AuthApi
import io.golos.data.api.auth.AuthApiImpl
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.api.communities.CommunitiesApiImpl
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.discussions.DiscussionsApiImpl
import io.golos.data.api.embed.EmbedApi
import io.golos.data.api.embed.EmbedApiImpl
import io.golos.data.api.events.EventsApi
import io.golos.data.api.events.EventsApiImpl
import io.golos.data.api.image_upload.ImageUploadApi
import io.golos.data.api.image_upload.ImageUploadApiImpl
import io.golos.data.api.push_notifications.PushNotificationsApi
import io.golos.data.api.push_notifications.PushNotificationsApiImpl
import io.golos.data.api.registration.RegistrationApi
import io.golos.data.api.registration.RegistrationApiImpl
import io.golos.data.api.settings.SettingsApi
import io.golos.data.api.settings.SettingsApiImpl
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.transactions.TransactionsApiImpl
import io.golos.data.api.user.UsersApi
import io.golos.data.api.user.UsersApiImpl
import io.golos.data.api.user_metadata.UserMetadataApi
import io.golos.data.api.user_metadata.UserMetadataApiImpl
import io.golos.data.api.vote.VoteApi
import io.golos.data.api.vote.VoteApiImpl
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.data.persistence.PreferenceManager
import io.golos.data.persistence.PreferenceManagerImpl
import io.golos.data.repositories.*
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryImpl
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.data.repositories.discussion.DiscussionRepositoryImpl
import io.golos.data.repositories.discussion.live_data.DiscussionCreationRepositoryLiveData
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryImpl
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryLiveData
import io.golos.data.repositories.users.UsersRepositoryImpl
import io.golos.data.repositories.vote.VoteRepository
import io.golos.data.repositories.vote.VoteRepositoryImpl
import io.golos.data.repositories.vote.live_data.VoteRepositoryLiveData
import io.golos.data.utils.ImageCompressor
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.*
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.domain.use_cases.user.UsersRepository
import io.golos.domain.mappers.*
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import javax.inject.Named

@Suppress("unused")
@Module
abstract class AppModuleBinds {
    @Binds
    @ApplicationScope
    abstract fun provideCyber(cyber: Cyber4JDagger): Commun4j

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
    abstract fun provideCyberPostToEntityMapper(mapper: CyberPostToEntityMapperImpl): CyberPostToEntityMapper

    @Binds
    abstract fun provideVoteToEntityMapper(mapper: VoteRequestModelToEntityMapperImpl): VoteRequestModelToEntityMapper

    @Binds
    abstract fun provideCyberFeedToEntityMapper(mapper: CyberFeedToEntityMapperImpl): CyberFeedToEntityMapper

    @Binds
    @ApplicationScope
    abstract fun providePostEntityToModelMapper(mapper: PostEntitiesToModelMapperImpl): PostEntitiesToModelMapper

    @Binds
    abstract fun provideFeedEntityToModelMapper(mapper: PostFeedEntityToModelMapperImpl): PostFeedEntityToModelMapper

    @Binds
    @ApplicationScope
    abstract fun provideVoteEntityToPostMapper(mapper: VoteRequestEntityToModelMapperImpl): VoteRequestEntityToModelMapper

    @Binds
    @ApplicationScope
    abstract fun provideCommentEntityToModelMapper(mapper: CommentEntityToModelMapperImpl): CommentEntityToModelMapper

    @Binds
    abstract fun provideCommentFeeEntityToModelMapper(mapper: CommentsFeedEntityToModelMapperImpl): CommentsFeedEntityToModelMapper

    @Binds
    abstract fun provideCyberCommentToEntityMapper(mapper: CyberCommentToEntityMapperImpl): CyberCommentToEntityMapper

    @Binds
    abstract fun provideCyberCommentFeedToEntityMapper(mapper: CyberCommentsToEntityMapperImpl): CyberCommentsToEntityMapper

    @Binds
    abstract fun provideCyberToAppErrorMapperImpl(mapper: CyberToAppErrorMapperImpl): CyberToAppErrorMapper

    @Binds
    @ApplicationScope
    abstract fun provideEventsToEntityMapper(mapper: EventsToEntityMapperImpl): EventsToEntityMapper

    @Binds
    abstract fun provideEventsEntityMerger(merger: EventsEntityMerger): EntityMerger<EventsListEntity>

    @Binds
    abstract fun provideEventsApprover(approver: EventsApprover): RequestApprover<EventsFeedUpdateRequest>

    @Binds
    abstract fun provideSettingsToEntityMapper(mapper: SettingsToEntityMapperImpl): SettingsToEntityMapper
    // endregion

    // region Api
    @Binds
    abstract fun provideAuthApi(service: AuthApiImpl): AuthApi

    @Binds
    abstract fun provideCommunitiesApi(api: CommunitiesApiImpl): CommunitiesApi

    @Binds
    abstract fun provideDiscussionsApi(api: DiscussionsApiImpl): DiscussionsApi

    @Binds
    abstract fun provideEmbedApi(api: EmbedApiImpl): EmbedApi

    @Binds
    abstract fun provideEventsApi(api: EventsApiImpl): EventsApi

    @Binds
    abstract fun provideImageUploadApi(api: ImageUploadApiImpl): ImageUploadApi

    @Binds
    abstract fun providePushNotificationsApi(api: PushNotificationsApiImpl): PushNotificationsApi

    @Binds
    abstract fun provideRegistrationApi(api: RegistrationApiImpl): RegistrationApi

    @Binds
    abstract fun provideSettingsApi(api: SettingsApiImpl): SettingsApi

    @Binds
    abstract fun provideTransactionsApi(api: TransactionsApiImpl): TransactionsApi

    @Binds
    abstract fun provideVoteApi(api: VoteApiImpl): VoteApi

    @Binds
    abstract fun provideUserMetadataApi(api: UserMetadataApiImpl): UserMetadataApi

    @Binds
    abstract fun provideUserApi(api: UsersApiImpl): UsersApi
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
    @ApplicationScope
    abstract fun provideDiscussionCreationRepository(repository: DiscussionRepositoryImpl): DiscussionRepository

    @Binds
    @ApplicationScope
    abstract fun provideEmbedsRepository(repository: EmbedsRepository): Repository<ProcessedLinksEntity, EmbedRequest>

    @Binds
    @ApplicationScope
    abstract fun provideAuthRepository(repository: AuthStateRepositoryImpl): AuthStateRepository

    @Binds
    @ApplicationScope
    abstract fun provideVoteRepositoryLiveData(repository: VoteRepositoryLiveData): Repository<VoteRequestEntity, VoteRequestEntity>

    @Binds
    abstract fun provideVoteRepository(repository: VoteRepositoryImpl): VoteRepository

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

    @Binds
    abstract fun provideCommunitiesRepository(repository: CommunitiesRepositoryImpl): CommunitiesRepository

    @Binds
    abstract fun provideUserRepository(repository: UsersRepositoryImpl): UsersRepository

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
    abstract fun provideDisplayInfoProvider(provider: DisplayInfoProviderImpl): DisplayInfoProvider

    @Binds
    @ApplicationScope
    abstract fun provideCrashlyticsFacade(facade: CrashlyticsFacadeImpl): CrashlyticsFacade

    @Binds
    @ApplicationScope
    abstract fun provideUIMonitor(monitor: UIMonitorImpl): UIMonitor

    @Binds
    @ApplicationScope
    abstract fun provideCurrentUserRepository(repository: CurrentUserRepositoryImpl): CurrentUserRepository

    @Binds
    @ApplicationScope
    abstract fun provideCurrentUserRepositoryRead(repository: CurrentUserRepositoryImpl): CurrentUserRepositoryRead

    @Binds
    @ApplicationScope
    abstract fun providePreferenceManager(preferenceManager: PreferenceManagerImpl): PreferenceManager

    @Binds
    abstract fun provideClipboardUtils(utils: ClipboardUtilsImpl): ClipboardUtils
}
