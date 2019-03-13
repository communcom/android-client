package io.golos.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.data.postsApiService
import io.golos.domain.*
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.DiscussionsSort
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class PostsFeedRepositoryTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val feedRepository = PostsFeedRepository(
        postsApiService,
        feedMapper, postMapper, postMerger, feedMerger, emptyPostFeedProducer,
        Dispatchers.Main, Dispatchers.Main, logger
    )

    @Test
    fun getFeedTest() {
        feedRepository.makeAction(CommunityFeedUpdateRequest("gls", 5, DiscussionsSort.FROM_OLD_TO_NEW, null))
        val posts =
            feedRepository.getAsLiveData(CommunityFeedUpdateRequest("gls", 0, DiscussionsSort.FROM_OLD_TO_NEW, null))
        println(posts)
    }
}