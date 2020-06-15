package io.golos.cyber_android.application.di

import dagger.Binds
import dagger.Module
import io.golos.commun4j.Commun4j
import io.golos.cyber_android.application.di_storage.Cyber4JDagger
import io.golos.cyber_android.application.shared.crashlytics.CrashlyticsFacadeImpl
import io.golos.cyber_android.application.shared.device_info.DeviceInfoProviderImpl
import io.golos.domain.fingerprint.FingerprintAuthManager
import io.golos.cyber_android.application.shared.fingerprints.FingerprintAuthManagerImpl
import io.golos.cyber_android.services.firebase.notifications.popup_manager.FirebaseNotificationPopupManager
import io.golos.cyber_android.services.firebase.notifications.popup_manager.FirebaseNotificationPopupManagerImpl
import io.golos.cyber_android.ui.shared.clipboard.ClipboardUtils
import io.golos.cyber_android.ui.shared.clipboard.ClipboardUtilsImpl
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostCreateEditRegistry
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostCreateEditRegistryImpl
import io.golos.cyber_android.ui.shared.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.ui.shared.utils.HtmlToSpannableTransformerImpl
import io.golos.cyber_android.ui.shared.utils.ImageCompressorImpl
import io.golos.data.ServerMessageReceiver
import io.golos.data.api.ServerMessageReceiverImpl
import io.golos.data.api.events.EventsApi
import io.golos.data.api.events.EventsApiImpl
import io.golos.data.api.image_upload.ImageUploadApi
import io.golos.data.api.image_upload.ImageUploadApiImpl
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.transactions.TransactionsApiImpl
import io.golos.data.api.user.UsersApi
import io.golos.data.api.user.UsersApiImpl
import io.golos.data.api.user_metadata.UserMetadataApi
import io.golos.data.api.user_metadata.UserMetadataApiImpl
import io.golos.data.encryption.aes.EncryptorAES
import io.golos.data.encryption.aes.EncryptorFingerprint
import io.golos.data.encryption.rsa.EncryptorRSA
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.network_state.NetworkStateCheckerImpl
import io.golos.data.persistence.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.data.persistence.key_value_storage.storages.Storage
import io.golos.data.persistence.key_value_storage.storages.StorageOperationsInstance
import io.golos.data.persistence.key_value_storage.storages.combined.CombinedStorage
import io.golos.data.persistence.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.data.persistence.user_keys_store.UserKeyStoreImpl
import io.golos.data.repositories.*
import io.golos.data.repositories.current_user.CurrentUserRepositoryImpl
import io.golos.data.repositories.discussion.DiscussionRepositoryImpl
import io.golos.data.repositories.discussion.live_data.DiscussionCreationRepositoryLiveData
import io.golos.data.repositories.embed.EmbedRepository
import io.golos.data.repositories.embed.EmbedRepositoryImpl
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryImpl
import io.golos.data.repositories.images_uploading.ImageUploadRepositoryLiveData
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.data.repositories.network_call.NetworkCallProxyImpl
import io.golos.data.repositories.settings.SettingsRepository
import io.golos.data.repositories.settings.SettingsRepositoryImpl
import io.golos.domain.SignUpTokensRepository
import io.golos.data.repositories.sign_up_tokens.SignUpTokensRepositoryImpl
import io.golos.data.repositories.users.UsersRepositoryImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.data.repositories.wallet.WalletRepositoryImpl
import io.golos.data.strings_converter.StringsConverterImpl
import io.golos.data.utils.ImageCompressor
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.*
import io.golos.domain.mappers.*
import io.golos.domain.repositories.*
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import io.golos.domain.use_cases.community.CommunitiesRepository
import javax.inject.Named

@Suppress("unused")
@Module
abstract class AppModuleBinds {
    @Binds
    @ApplicationScope
    abstract fun provideCyber(cyber: Cyber4JDagger): Commun4j

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
    // endregion

    @Binds
    abstract fun provideEventsApi(api: EventsApiImpl): EventsApi

    @Binds
    abstract fun provideImageUploadApi(api: ImageUploadApiImpl): ImageUploadApi

    @Binds
    abstract fun provideTransactionsApi(api: TransactionsApiImpl): TransactionsApi

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
    abstract fun provideImageUploadRepositoryLiveData(repository: ImageUploadRepositoryLiveData): Repository<UploadedImagesEntity, ImageUploadRequest>

    @Binds
    abstract fun provideImageUploadRepository(repository: ImageUploadRepositoryImpl): ImageUploadRepository

    @Binds
    abstract fun provideEventsRepository(repository: EventsRepository): Repository<EventsListEntity, EventsFeedUpdateRequest>

    @Binds
    @ApplicationScope
    abstract fun provideUserMetadataRepository(repository: UserMetadataRepository): Repository<UserMetadataCollectionEntity, UserMetadataRequest>

    @Binds
    abstract fun provideCommunitiesRepository(repository: CommunitiesRepositoryImpl): CommunitiesRepository

    @Binds
    abstract fun provideUserRepository(repository: UsersRepositoryImpl): UsersRepository

    @Binds
    abstract fun provideWalletRepository(repository: WalletRepositoryImpl): WalletRepository

    @Binds
    abstract fun provideEmbedRepository(repository: EmbedRepositoryImpl): EmbedRepository

    @Binds
    abstract fun provideSettingsRepository(repository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun provideAuthRepository(repository: AuthRepositoryImpl): AuthRepository
    // endregion

    @Binds
    abstract fun provideDeviceInfoService(service: DeviceInfoProviderImpl): DeviceInfoProvider

    @Binds
    @ApplicationScope
    abstract fun provideCrashlyticsFacade(facade: CrashlyticsFacadeImpl): CrashlyticsFacade

    @Binds
    @ApplicationScope
    abstract fun provideCurrentUserRepository(repository: CurrentUserRepositoryImpl): CurrentUserRepository

    @Binds
    @ApplicationScope
    abstract fun provideCurrentUserRepositoryRead(repository: CurrentUserRepositoryImpl): CurrentUserRepositoryRead

    @Binds
    @ApplicationScope
    abstract fun provideNotificationsRepository(repository: NotificationsRepositoryImpl): NotificationsRepository

    @Binds
    abstract fun provideSignUpTokensRepository(repository: SignUpTokensRepositoryImpl): SignUpTokensRepository

    @Binds
    abstract fun provideClipboardUtils(utils: ClipboardUtilsImpl): ClipboardUtils

    @Binds
    abstract fun provideNetworkStateChecker(checker: NetworkStateCheckerImpl): NetworkStateChecker

    @Binds
    abstract fun provideNetworkCallProxy(proxy: NetworkCallProxyImpl): NetworkCallProxy

    @Binds
    @ApplicationScope
    abstract fun provideServerMessageReceiver(receiver: ServerMessageReceiverImpl): ServerMessageReceiver
    @Binds
    @ApplicationScope
    abstract fun provideFirebaseNotificationPopupManager(manager: FirebaseNotificationPopupManagerImpl): FirebaseNotificationPopupManager

    @Binds
    @ApplicationScope
    abstract fun providePostCreateEditDataPass(dataPass: PostCreateEditRegistryImpl): PostCreateEditRegistry
}
