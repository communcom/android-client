package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.golos.cyber4j.Cyber4J
import io.golos.cyber_android.CommunityFeedViewModel
import io.golos.data.Cyber4jApiService
import io.golos.data.PostsApiService
import io.golos.data.repositories.PostsFeedRepository
import io.golos.domain.Logger
import io.golos.domain.interactors.CommunityFeedUseCase
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.rules.*
import kotlinx.coroutines.Dispatchers

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class ServiceLocatorImpl(private val appContext: Context) : ServiceLocator {

    private val cyber4j by lazy { Cyber4J() }

    private val postsApiService: PostsApiService by lazy { Cyber4jApiService(cyber4j) }

    private val postMapper = PostMapper()
    private val feedMapper = PostsFeedMapper(postMapper)

    private val postMerger = PostMerger()
    private val feedMerger = PostFeedMerger()

    private val emptyPostFeedProducer = EmptyPostFeedProducer()

    private val logger = object : Logger {
        override fun invoke(e: Exception) {
            e.printStackTrace()
        }
    }

    private val communityFeedRepository by lazy {
        PostsFeedRepository(
            postsApiService,
            feedMapper,
            postMapper,
            postMerger,
            feedMerger,
            emptyPostFeedProducer,
            Dispatchers.Main,
            Dispatchers.Default,
            logger
        )
    }

    override fun getCommunityFeedViewModelFactory(communityModel: CommunityModel): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    CommunityFeedViewModel::class.java -> CommunityFeedViewModel(
                        CommunityFeedUseCase(
                            communityModel,
                            communityFeedRepository
                        )
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    override val getAppContext: Context
        get() = appContext

}