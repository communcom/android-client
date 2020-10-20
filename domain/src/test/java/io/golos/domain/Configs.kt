package io.golos.domain

import io.golos.cyber4j.Cyber4J
import io.golos.domain.mappers.CyberFeedToEntityMapperImpl
import io.golos.domain.mappers.CyberPostToEntityMapperImpl
import io.golos.domain.mappers.PostEntitiesToModelMapperImpl
import io.golos.domain.mappers.PostFeedEntityToModelMapperImpl
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
internal val cyber4j by lazy { Cyber4J() }


internal val cyberPostToEntityMapper = CyberPostToEntityMapperImpl()
internal val postEntityToModelMapper = PostEntitiesToModelMapperImpl()

internal val cyberFeedToEntityMapper = CyberFeedToEntityMapperImpl(cyberPostToEntityMapper)
internal val feedEntityToModelMapper = PostFeedEntityToModelMapperImpl(postEntityToModelMapper)

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
    override val calculationsDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
}

