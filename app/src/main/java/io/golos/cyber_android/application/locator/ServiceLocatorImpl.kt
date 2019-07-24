package io.golos.cyber_android.application.locator

import android.app.backup.BackupManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber4j.Cyber4J
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.logger.LoggerImpl
import io.golos.cyber_android.core.backup.facade.BackupKeysFacade
import io.golos.cyber_android.core.backup.facade.BackupKeysFacadeImpl
import io.golos.cyber_android.core.backup.facade.BackupKeysFacadeSync
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.cyber_android.core.encryption.aes.EncryptorFingerprint
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManagerImpl
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.combined.CombinedStorage
import io.golos.cyber_android.core.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.cyber_android.core.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.domain.UserKeyStore
import io.golos.cyber_android.core.user_keys_store.UserKeyStoreImpl
import io.golos.cyber_android.fcm.FcmTokenProviderImpl
import io.golos.cyber_android.ui.common.calculator.UICalculatorImpl
import io.golos.cyber_android.ui.common.helper.UIHelperImpl
import io.golos.cyber_android.ui.screens.communities.community.CommunityFeedViewModel
import io.golos.cyber_android.ui.screens.editor.EditorPageViewModel
import io.golos.cyber_android.ui.screens.feed.UserSubscriptionsFeedViewModel
import io.golos.cyber_android.ui.screens.login.AuthViewModel
import io.golos.cyber_android.ui.screens.login.signin.qr_code.QrCodeSignInViewModel
import io.golos.cyber_android.ui.screens.login.signin.qr_code.keys_extractor.QrCodeKeysExtractor
import io.golos.cyber_android.ui.screens.login.signin.qr_code.keys_extractor.QrCodeKeysExtractorImpl
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor.MasterPassKeysExtractorImpl
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeViewModel
import io.golos.cyber_android.ui.screens.login.signin.user_name.UserNameSignInViewModel
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.login.signup.country.SignUpCountryViewModel
import io.golos.cyber_android.ui.screens.login.signup.keys_backup.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.login.signup.onboarding.image.OnboardingUserImageViewModel
import io.golos.cyber_android.ui.screens.main.MainViewModel
import io.golos.cyber_android.ui.screens.notifications.NotificationsViewModel
import io.golos.cyber_android.ui.screens.post.PostPageViewModel
import io.golos.cyber_android.ui.screens.profile.ProfileViewModel
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarViewModel
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditProfileBioViewModel
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverViewModel
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsViewModel
import io.golos.cyber_android.ui.screens.profile.posts.UserPostsFeedViewModel
import io.golos.cyber_android.utils.FromSpannedToHtmlTransformerImpl
import io.golos.cyber_android.utils.HtmlToSpannableTransformerImpl
import io.golos.cyber_android.utils.ImageCompressorImpl
import io.golos.data.api.Cyber4jApiService
import io.golos.data.errors.CyberToAppErrorMapperImpl
import io.golos.data.repositories.*
import io.golos.domain.*
import io.golos.domain.entities.*
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.*
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCaseImpl
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import io.golos.sharedmodel.Cyber4JConfig
import io.golos.sharedmodel.CyberName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class ServiceLocatorImpl(private val appContext: Context) : ServiceLocator, RepositoriesHolder {
    // [Dagger] - done
    private val cyber4jConfigs = mapOf(
        "stable" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://116.202.4.39:8888/",
            servicesUrl = "ws://116.203.98.241:8080"
        ),
        "dev" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://46.4.96.246:8888/",
            servicesUrl = "ws://159.69.33.136:8080"
        ),
        "unstable" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://116.202.4.46:8888/",
            servicesUrl = "ws://159.69.33.136:8080"
        )
    )

    // [Dagger] - done
    private val cyber4j by lazy { Cyber4J(cyber4jConfigs[BuildConfig.FLAVOR] ?: Cyber4JConfig()) }

    // [Dagger] - done
    private val apiService: Cyber4jApiService by lazy { Cyber4jApiService(cyber4j) }

    // [Dagger] - done
    private val cyberPostToEntityMapper = CyberPostToEntityMapper()

    // [Dagger] - done
    private val voteToEntityMapper = VoteRequestModelToEntityMapper()

    // [Dagger] - done
    private val cyberFeedToEntityMapper = CyberFeedToEntityMapper(cyberPostToEntityMapper)

    // [Dagger] - done
    private val fromHtmlTransformet = HtmlToSpannableTransformerImpl()

    // [Dagger] - done
    private val fromSpannableToHtml = FromSpannedToHtmlTransformerImpl()

    // [Dagger] - done
    private val deviceIdProvider = MyDeviceIdProvider(appContext)

    // [Dagger] - done
    private val postEntityToModelMapper = PostEntityEntitiesToModelMapper(fromHtmlTransformet)
    // [Dagger] - done
    private val feedEntityToModelMapper = PostFeedEntityToModelMapper(postEntityToModelMapper)
    // [Dagger] - done
    private val voteEntityToPostMapper = VoteRequestEntityToModelMapper()
    // [Dagger] - done
    private val commentEntityToModelMapper = CommentEntityToModelMapper(fromHtmlTransformet)
    // [Dagger] - done
    private val commentFeeEntityToModelMapper = CommentsFeedEntityToModelMapper(commentEntityToModelMapper)
    // [Dagger] - done
    private val toCountriesModelMapper = CountryEntityToModelMapper()
    // [Dagger] - done
    private val toRegistrationMapper = UserRegistrationStateEntityMapper()

    // [Dagger] - done
    private val approver = FeedUpdateApprover()
    // [Dagger] - done
    private val commentApprover = CommentUpdateApprover()

    // -----------------  [Dagger] - done ----------------------
    private val postMerger = PostMerger()
    private val feedMerger = PostFeedMerger()

    private val emptyPostFeedProducer = EmptyPostFeedProducer()

    private val commentMerger = CommentMerger()
    private val commentFeedMerger = CommentFeedMerger()

    private val emptyCommentFeedProducer = EmptyCommentFeedProducer()
    // -----------------  [Dagger] - done ----------------------

    // -----------------  [Dagger] - done ----------------------
    private val fromIframelyMapper = IfremlyEmbedMapper()
    private val fromOEmbedMapper = OembedMapper()

    private val cyberCommentToEntityMapper = CyberCommentToEntityMapper()
    private val cyberCommentFeedToEntityMapper = CyberCommentsToEntityMapper(cyberCommentToEntityMapper)

    private val discussionCreationToEntityMapper = DiscussionCreateResultToEntityMapper()
    private val discussionUpdateToEntityMapper = DiscussionUpdateResultToEntityMapper()
    private val discussionDeleteToEntityMapper = DiscussionDeleteResultToEntityMapper()
    private val discussionEntityRequestToApiRequestMapper = RequestEntityToArgumentsMapper()

    private val toAppErrorMapper = CyberToAppErrorMapperImpl()
    // -----------------  [Dagger] - done ----------------------

    // [Dagger] - done
    override val userKeyStore: UserKeyStore
        get() = UserKeyStoreImpl(keyValueStorage, stringsConverter, encryptor)

    // [Dagger] - done
    private val logger = LoggerImpl()

    // [Dagger] - done
    override val stringsConverter: StringsConverter
        get() = StringsConverterImpl()

    // [Dagger] - done
    override val keyValueStorage: KeyValueStorageFacade by lazy {
        KeyValueStorageFacadeImpl(CombinedStorage(InMemoryStorage(), SharedPreferencesStorage(appContext)), moshi)
    }

    // [Dagger] - done
    override val encryptor: Encryptor by lazy {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptorAES()
        } else  {
            EncryptorAESOldApi(keyValueStorage, EncryptorRSA(appContext))
        }
    }

    // [Dagger] - done
    override val masterPassKeysExtractor: MasterPassKeysExtractor by lazy {
        MasterPassKeysExtractorImpl(
            userKeyStore,
            dispatchersProvider,
            logger
        )
    }

    // [Dagger] - done
    override val qrCodeKeysExtractor: QrCodeKeysExtractor by lazy {
        QrCodeKeysExtractorImpl(
            userKeyStore,
            dispatchersProvider,
            logger
        )
    }

    // [Dagger] - done
    private val fingerprintAuthManager: FingerprintAuthManager by lazy {
        FingerprintAuthManagerImpl(appContext, encryptor as EncryptorFingerprint)
    }

    // [Dagger] - done
    override val backupManager: BackupManager by lazy { BackupManager(appContext) }

    // [Dagger] - done
    override val backupKeysFacade: BackupKeysFacade
        get() = BackupKeysFacadeImpl(appContext, encryptor, backupManager)

    // [Dagger] - done
    override val backupKeysFacadeSync: BackupKeysFacadeSync
        get() = BackupKeysFacadeImpl(appContext, encryptor, backupManager)

    // [Dagger] - done
    override val dispatchersProvider = object : DispatchersProvider {
        override val uiDispatcher: CoroutineDispatcher
            get() = Dispatchers.Main
        override val workDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.IO
    }

    // [Dagger] - done
    override val moshi: Moshi by lazy { Moshi.Builder().build() }

    // [Dagger] - done
    override val uiCalculator
        get() = UICalculatorImpl(appContext)

    // [Dagger] - done
    override val uiHelper
        get() = UIHelperImpl(appContext)

    // [Dagger] - done
    override val postFeedRepository: AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>by lazy {
        PostsFeedRepository(
            apiService,
            cyberFeedToEntityMapper,
            cyberPostToEntityMapper,
            postMerger,
            feedMerger,
            approver,
            emptyPostFeedProducer,
            dispatchersProvider,
            logger
        )
    }

    // [Dagger] - done
    override val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest> by lazy {
        CommentsFeedRepository(
            apiService,
            cyberCommentFeedToEntityMapper,
            cyberCommentToEntityMapper,
            commentMerger,
            commentFeedMerger,
            commentApprover,
            emptyCommentFeedProducer,
            dispatchersProvider,
            logger
        )
    }

    // [Dagger] - done
    override val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>
            by lazy {
                DiscussionCreationRepository(
                    apiService,
                    apiService,
                    dispatchersProvider,
                    logger,
                    discussionEntityRequestToApiRequestMapper,
                    discussionCreationToEntityMapper,
                    discussionUpdateToEntityMapper,
                    discussionDeleteToEntityMapper,
                    toAppErrorMapper
                )
            }

    // [Dagger] - done
    override val embedsRepository: Repository<ProcessedLinksEntity, EmbedRequest>
            by lazy {
                EmbedsRepository(apiService, dispatchersProvider, logger, fromIframelyMapper, fromOEmbedMapper)
            }

    // [Dagger] - done
    override val authRepository: Repository<AuthState, AuthRequest>
            by lazy { AuthStateRepository(apiService, dispatchersProvider, logger, keyValueStorage, userKeyStore) }

    // [Dagger] - done
    override val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
            by lazy {
                VoteRepository(apiService, apiService, dispatchersProvider, toAppErrorMapper, logger)
            }

    // [Dagger] - done
    override val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>
            by lazy {
                RegistrationRepository(
                    apiService, dispatchersProvider, logger, toRegistrationMapper, toAppErrorMapper
                )
            }

    // [Dagger] - done
    override val countriesRepository: Repository<CountriesList, CountriesRequest>
            by lazy {
                CountriesRepository(
                    dispatchersProvider,
                    object : CountriesProvider {
                        override suspend fun getAllCountries(): List<CountryEntity> {
                            val contriesList =
                                appContext.resources.openRawResource(R.raw.countries).readBytes().let { String(it) }
                            return moshi.adapter<List<CountryEntity>>(
                                Types.newParameterizedType(
                                    List::class.java,
                                    CountryEntity::class.java
                                )
                            ).fromJson(contriesList)!!
                        }
                    },
                    logger
                )
            }

    // [Dagger] - done
    override val settingsRepository: Repository<UserSettingEntity, SettingChangeRequest>
            by lazy {
                SettingsRepository(
                    apiService,
                    SettingsToEntityMapper(moshi),
                    SettingToCyberMapper(),
                    dispatchersProvider,
                    deviceIdProvider,
                    MyDefaultSettingProvider(),
                    logger
                )
            }

    // [Dagger] - done
    override val imageUploadRepository: Repository<UploadedImagesEntity, ImageUploadRequest>
            by lazy { ImageUploadRepository(apiService, dispatchersProvider, ImageCompressorImpl, logger) }

    // [Dagger] - done
    override val eventsRepository: Repository<EventsListEntity, EventsFeedUpdateRequest>
            by lazy {
                EventsRepository(
                    apiService, EventsToEntityMapper(), EventsEntityMerger(), EventsApprover(),
                    dispatchersProvider, logger
                )
            }

    // [Dagger] - done
    override val userMetadataRepository: Repository<UserMetadataCollectionEntity, UserMetadataRequest>
            by lazy {
                UserMetadataRepository(
                    apiService,
                    apiService,
                    dispatchersProvider,
                    logger,
                    UserMetadataToEntityMapper(),
                    toAppErrorMapper
                )
            }

    // [Dagger] - done
    override val pushesRepository: Repository<PushNotificationsStateEntity, PushNotificationsStateUpdateRequest>
            by lazy {
                PushNotificationsRepository(apiService, deviceIdProvider,
                    FcmTokenProviderImpl, toAppErrorMapper, logger, dispatchersProvider
                )
            }

    // [Dagger] - done
    override fun getCommunityFeedViewModelFactory(
        communityId: CommunityId,
        forUser: CyberName
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    // [Dagger] - done
                    CommunityFeedViewModel::class.java -> CommunityFeedViewModel(
                        getCommunityFeedUseCase(communityId),
                        getVoteUseCase(),
                        getDiscussionPosterUseCase(),
                        getUserMetadataUseCase(forUser),
                        getSignInUseCase()
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // [Dagger] - done
    override fun getUserPostsFeedViewModelFactory(forUser: CyberUser): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    UserPostsFeedViewModel::class.java -> UserPostsFeedViewModel(
                        getUserPostFeedUseCase(forUser),
                        getVoteUseCase(),
                        getDiscussionPosterUseCase(),
                        getSignInUseCase()
                    ) as T


                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // [Dagger] - done
    override fun getUserSubscriptionsFeedViewModelFactory(
        forUser: CyberUser,
        appUser: CyberName
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    UserSubscriptionsFeedViewModel::class.java -> UserSubscriptionsFeedViewModel(
                        getUserSubscriptionsFeedUseCase(forUser),
                        getVoteUseCase(),
                        getDiscussionPosterUseCase(),
                        getUserMetadataUseCase(appUser),
                        getSignInUseCase()
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // [Dagger] - done
    override fun getPostWithCommentsViewModelFactory(postId: DiscussionIdModel): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    PostPageViewModel::class.java -> PostPageViewModel(
                        getPostWithCommentsUseCase(postId),
                        getVoteUseCase(),
                        getDiscussionPosterUseCase(),
                        getSignInUseCase()
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    override fun getDefaultViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    UserNameSignInViewModel::class.java -> UserNameSignInViewModel(
                        getSignInUseCase(),
                        masterPassKeysExtractor,
                        dispatchersProvider,
                        backupKeysFacade
                    ) as T

                    QrCodeSignInViewModel::class.java -> QrCodeSignInViewModel(
                        getSignInUseCase(),
                        qrCodeKeysExtractor,
                        dispatchersProvider
                    ) as T

                    AuthViewModel::class.java -> AuthViewModel(
                        getSignInUseCase()
                    ) as T

                    SignUpCountryViewModel::class.java -> SignUpCountryViewModel(
                        dispatchersProvider,
                        getCountriesChooserUseCase()
                    ) as T

                    SignUpViewModel::class.java -> SignUpViewModel(
                        getSignOnUseCase(object : TestPassProvider {
                            override fun provide() = BuildConfig.AUTH_TEST_PASS
                        })
                    ) as T

                    NotificationsViewModel::class.java -> NotificationsViewModel(
                        getEventsUseCase()
                    ) as T

                    ProfileSettingsViewModel::class.java -> ProfileSettingsViewModel(
                        getSettingUserCase(),
                        getPushNotificationsSettingsUseCase(),
                        getSignInUseCase()
                    ) as T

                    MainViewModel::class.java -> MainViewModel(
                        getSignInUseCase(),
                        getEventsUseCase(),
                        getPushNotificationsSettingsUseCase()
                    ) as T

                    SignUpProtectionKeysViewModel::class.java -> SignUpProtectionKeysViewModel(
                        userKeyStore,
                        keyValueStorage,
                        dispatchersProvider,
                        apiService,
                        logger,
                        backupKeysFacade) as T

                    PinCodeViewModel::class.java -> PinCodeViewModel(dispatchersProvider, getPinCodeModel()) as T

                    FingerprintViewModel::class.java -> FingerprintViewModel(dispatchersProvider, getFingerprintModel()) as T

                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // [Dagger] - done
    override fun getEditorPageViewModelFactory(
        community: CommunityModel?,
        postToEdit: DiscussionIdModel?
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    EditorPageViewModel::class.java -> EditorPageViewModel(
                        getEmbedsUseCase(),
                        getDiscussionPosterUseCase(),
                        getImageUploadUseCase(),
                        community,
                        postToEdit,
                        if (postToEdit != null) getPostWithCommentsUseCase(postToEdit) else null
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // Split to Onboarding & profile !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun getViewModelFactoryByCyberName(forUser: CyberName): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    EditProfileCoverViewModel::class.java -> EditProfileCoverViewModel(
                        getUserMetadataUseCase(forUser),
                        getImageUploadUseCase(),
                        dispatchersProvider
                    ) as T

                    EditProfileAvatarViewModel::class.java -> EditProfileAvatarViewModel(
                        getUserMetadataUseCase(forUser),
                        getImageUploadUseCase(),
                        dispatchersProvider
                    ) as T

                    EditProfileBioViewModel::class.java -> EditProfileBioViewModel(
                        getUserMetadataUseCase(forUser)
                    ) as T

                    ProfileViewModel::class.java -> ProfileViewModel(
                        getUserMetadataUseCase(forUser),
                        getSignInUseCase(),
                        forUser
                    ) as T
                    OnboardingUserImageViewModel::class.java -> OnboardingUserImageViewModel(getUserMetadataUseCase(forUser)) as T

                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    // [Dagger] - done
    override fun getCommunityFeedUseCase(communityId: CommunityId): CommunityFeedUseCase {
        return CommunityFeedUseCase(
            communityId,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }

    // [Dagger] - done
    override fun getUserSubscriptionsFeedUseCase(user: CyberUser): UserSubscriptionsFeedUseCase {
        return UserSubscriptionsFeedUseCase(
            user,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }

    // [Dagger] - done
    override fun getUserPostFeedUseCase(user: CyberUser): UserPostFeedUseCase {
        return UserPostFeedUseCase(
            user,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }


    // [Dagger] - done
    override fun getVoteUseCase() =
        VoteUseCase(
            authRepository, voteRepository,
            dispatchersProvider, voteEntityToPostMapper,
            voteToEntityMapper
        )

    // [Dagger] - done
    override fun getPostWithCommentsUseCase(postId: DiscussionIdModel): PostWithCommentUseCase {
        return PostWithCommentUseCase(
            postId,
            postFeedRepository,
            postEntityToModelMapper,
            commentsRepository,
            voteRepository,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
    }

    // [Dagger] - done
    override fun getDiscussionPosterUseCase(): DiscussionPosterUseCase {
        return DiscussionPosterUseCase(discussionCreationRepository, dispatchersProvider, fromSpannableToHtml)
    }

    // [Dagger] - done
    override val getAppContext: Context
        get() = appContext

    // [Dagger] - done
    override fun getSignInUseCase(): SignInUseCase {
        return SignInUseCase(authRepository, dispatchersProvider)
    }

    // [Dagger] - done
    override fun getSignOnUseCase(
        testPassProvider: TestPassProvider
    ): SignUpUseCase {
        return SignUpUseCase(
            registrationRepository,
            authRepository,
            dispatchersProvider,
            testPassProvider,
            userKeyStore
        )
    }

    // [Dagger] - done
    override fun getEmbedsUseCase(): EmbedsUseCase {
        return EmbedsUseCase(dispatchersProvider, embedsRepository)
    }

    // [Dagger] - done
    override fun getCountriesChooserUseCase(): CountriesChooserUseCase {
        return CountriesChooserUseCase(countriesRepository, toCountriesModelMapper, dispatchersProvider)
    }

    // [Dagger] - done
    override fun getSettingUserCase(): SettingsUseCase {
        return SettingsUseCase(settingsRepository, authRepository)
    }

    // [Dagger] - done
    override fun getImageUploadUseCase(): ImageUploadUseCase {
        return ImageUploadUseCase(imageUploadRepository)
    }

    override fun getEventsUseCase(): EventsUseCase {
        return EventsUseCase(
            eventsRepository,
            authRepository,
            EventEntityToModelMapper(),
            dispatchersProvider
        )
    }

    // [Dagger] - done (for MyFeed only)
    override fun getUserMetadataUseCase(forUser: CyberName): UserMetadataUseCase {
        return UserMetadataUseCase(forUser, userMetadataRepository)
    }

    // [Dagger] - done
    override fun getPushNotificationsSettingsUseCase(): PushNotificationsSettingsUseCase {
        return PushNotificationsSettingsUseCaseImpl(pushesRepository, authRepository, keyValueStorage)
    }

    // [Dagger] - done
    override fun getPinCodeModel(): PinCodeModel =
        PinCodeModelImpl(
            dispatchersProvider,
            stringsConverter,
            encryptor,
            keyValueStorage,
            logger,
            fingerprintAuthManager)

    // [Dagger] - done
    override fun getFingerprintModel(): FingerprintModel =
        FingerprintModelImpl(
            keyValueStorage,
            logger,
            dispatchersProvider)
}