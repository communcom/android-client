package io.golos.cyber_android

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber4j.Cyber4J
import io.golos.cyber_android.interactors.CountriesChooserUseCaseTest
import io.golos.cyber_android.locator.RepositoriesHolder
import io.golos.data.api.Cyber4jApiService
import io.golos.data.repositories.*
import io.golos.domain.*
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.CountryEntityToModelMapper
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.*
import java.util.concurrent.Executor

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
val cyber4j by lazy { Cyber4J() }

val apiService: Cyber4jApiService by lazy { Cyber4jApiService(cyber4j) }

val cyberPostToEntityMapper = CyberPostToEntityMapper()
val voteToEntityMapper = VoteRequestModelToEntityMapper()
val cyberFeedToEntityMapper = CyberFeedToEntityMapper(cyberPostToEntityMapper)

val postEntityToModelMapper = PostEntityEntitiesToModelMapper(object : HtmlToSpannableTransformer {
    override fun transform(html: String): CharSequence {
        return html
    }
})
val feedEntityToModelMapper = PostFeedEntityToModelMapper(postEntityToModelMapper)
val commentEntityToModelMapper = CommentEntityToModelMapper(object : HtmlToSpannableTransformer {
    override fun transform(html: String): CharSequence {
        return html
    }
})
val commentFeeEntityToModelMapper = CommentsFeedEntityToModelMapper(commentEntityToModelMapper)
val voteEntityToPostMapper = VoteRequestEntityToModelMapper()

val toCountryModelMapper = CountryEntityToModelMapper()

val fromIframelyMapper = IfremlyEmbedMapper()
val fromOEmbedMapper = OembedMapper()

val approver = FeedUpdateApprover()

val postMerger = PostMerger()
val feedMerger = PostFeedMerger()

val emptyPostFeedProducer = EmptyPostFeedProducer()


val cyberCommentToEntityMapper = CyberCommentToEntityMapper()
val cyberCommentFeedToEntityMapper = CyberCommentsToEntityMapper(cyberCommentToEntityMapper)

val toRegistrationMapper = UserRegistrationStateEntityMapper()

val commentApprover = CommentUpdateApprover()

val commentMerger = CommentMerger()
val commentFeedMerger = CommentFeedMerger()

val emptyCommentFeedProducer = EmptyCommentFeedProducer()

val discussionCreationToEntityMapper = DiscussionCreateResultToEntityMapper()
val discussionEntityRequestToApiRequestMapper = RequestEntityToArgumentsMapper()


val logger = object : Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}

val dispatchersProvider = object : DispatchersProvider {
    override val uiDispatcher: CoroutineDispatcher
        get() = Executor { command -> command?.run() }.asCoroutineDispatcher()
    override val workDispatcher: CoroutineDispatcher
        get() = Executor { command -> command?.run() }.asCoroutineDispatcher()
    override val networkDispatcher: CoroutineDispatcher
        get() = Executor { command -> command?.run() }.asCoroutineDispatcher()
}

val feedRepository = PostsFeedRepository(
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

val commentsFeedRepository = CommentsFeedRepository(
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

val embededsRepository = EmbedsRepository(
    apiService, dispatchersProvider, logger,
    fromIframelyMapper, fromOEmbedMapper
)


private val persister = object : Persister {
    override fun saveAuthState(state: AuthState) {

    }

    override fun getAuthState(): AuthState? {

        return AuthState(CyberUser("destroyer2k@golos"), true)
    }

    override fun saveActiveKey(activeKey: String) {

    }

    override fun getActiveKey(): String? {
        return "5JagnCwCrB2sWZw6zCvaBw51ifoQuNaKNsDovuGz96wU3tUw7hJ"
    }
}
val authStateRepository = AuthStateRepository(apiService, dispatchersProvider, logger, persister)

val voteRepo = VoteRepository(apiService, dispatchersProvider, logger)

val discussionCreationRepo = DiscussionCreationRepository(
    apiService, dispatchersProvider,
    logger,
    discussionEntityRequestToApiRequestMapper,
    discussionCreationToEntityMapper
)

val countriesRepo: Repository<CountriesList, CountriesRequest>
        by lazy {
            CountriesRepository(
                dispatchersProvider,
                object : CountriesProvider {

                    override suspend fun getAllCountries(): List<CountryEntity> {
                        println("getAllCountries")
                        val contriesList =
                            CountriesChooserUseCaseTest::class.java.classLoader.getResource("countries.json").readText()
                        return Moshi.Builder().build().adapter<List<CountryEntity>>(
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

val regRepo: Repository<UserRegistrationStateEntity, RegistrationStepRequest>
        by lazy {
            RegistrationRepository(
                apiService, dispatchersProvider, logger,
                toRegistrationMapper
            )
        }
val imageUploadRepo = ImageUploadRepository(apiService, dispatchersProvider, logger)

val settingsRepo = SettingsRepository(
    apiService,
    SettingsToEntityMapper(Moshi.Builder().build()),
    SettingToCyberMapper(),
    dispatchersProvider,
    object : DeviceIdProvider {
        private val id = UUID.randomUUID().toString()
        override fun provide(): String {
            return id
        }
    },
    MyDefaultSettingProvider(),
    logger
)
val eventsRepos: Repository<EventsListEntity, EventsFeedUpdateRequest>
        by lazy {
            EventsRepository(
                apiService, EventsToEntityMapper(), EventsEntityMerger(), EventsApprover(),
                dispatchersProvider, logger
            )
        }


val appCore = AppCore(object : RepositoriesHolder {
    override val postFeedRepository: AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>
        get() = feedRepository
    override val authRepository: Repository<AuthState, AuthRequest>
        get() = authStateRepository
    override val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
        get() = voteRepo
    override val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>
        get() = commentsFeedRepository
    override val embedsRepository: Repository<ProcessedLinksEntity, EmbedRequest>
        get() = embededsRepository
    override val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>
        get() = discussionCreationRepo
    override val countriesRepository: Repository<CountriesList, CountriesRequest>
        get() = countriesRepo
    override val registrationRepository: Repository<UserRegistrationStateEntity, RegistrationStepRequest>
        get() = regRepo
    override val settingsRepository: Repository<UserSettingEntity, SettingChangeRequest>
        get() = settingsRepo
    override val imageUploadRepository: Repository<UploadedImagesEntity, ImageUploadRequest>
        get() = imageUploadRepo
    override val eventsRepository: Repository<EventsListEntity, EventsFeedUpdateRequest>
        get() = eventsRepos
}, dispatchersProvider)


