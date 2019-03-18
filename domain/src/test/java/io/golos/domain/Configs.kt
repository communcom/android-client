package io.golos.domain

import io.golos.cyber4j.Cyber4J
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
internal val cyber4j by lazy { Cyber4J() }


internal val cyberPostToEntityMapper = CyberPostToEntityMapper()
internal val postEntityToModelMapper = PostEntityToModelMapper()

internal val cyberFeedToEntityMapper = CyberFeedToEntityMapper(cyberPostToEntityMapper)
internal val feedEntityToModelMapper = PostFeedEntityToModelMapper(postEntityToModelMapper)

internal val postMerger = PostMerger()
internal val feedMerger = PostFeedMerger()

internal val emptyPostFeedProducer = EmptyPostFeedProducer()

internal val logger = object : Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}

internal val dispatchersProvider = object : DispatchersProvider {
    override val uiDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val workDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
}

