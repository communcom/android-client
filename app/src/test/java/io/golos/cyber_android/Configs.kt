package io.golos.cyber_android

import io.golos.cyber4j.Cyber4J
import io.golos.data.Cyber4jApiService
import io.golos.data.repositories.PostsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
val cyber4j by lazy { Cyber4J() }

val postsApiService = Cyber4jApiService(Cyber4J())

val cyberPostToEntityMapper = CyberPostToEntityMapper()
val postEntityToModelMapper = PostEntityToModelMapper()

val cyberFeedToEntityMapper = CyberFeedToEntityMapper(cyberPostToEntityMapper)
val feedEntityToModelMapper = PostFeedEntityToModelMapper(postEntityToModelMapper)

val postMerger = PostMerger()
val feedMerger = PostFeedMerger()

val emptyPostFeedProducer = EmptyPostFeedProducer()

val logger = object : Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}
private val exc = Executors.newSingleThreadExecutor()

val dispatchersProvider = object : DispatchersProvider {
    override val uiDispatcher: CoroutineDispatcher
        get() = Executor { command -> command?.run() }.asCoroutineDispatcher()
    override val workDispatcher: CoroutineDispatcher
        get() = Executor { command -> command?.run() }.asCoroutineDispatcher()
}

val feedRepository = PostsFeedRepository(
    postsApiService,
    cyberFeedToEntityMapper,
    cyberPostToEntityMapper,
    postMerger,
    feedMerger,
    emptyPostFeedProducer,
    dispatchersProvider.uiDispatcher, dispatchersProvider.workDispatcher,
    logger
)
