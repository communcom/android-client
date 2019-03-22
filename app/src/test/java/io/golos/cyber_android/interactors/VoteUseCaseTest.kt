package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.*
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
class VoteUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    val voteUseCase =
        VoteUseCase(
            authStateRepository, voteRepo, feedRepository,
            dispatchersProvider, voteEntityToPostMapper,
            voteToEntityMapper
        )

    val postRequest = CommunityFeedUpdateRequest("gls", 20, DiscussionsSort.FROM_NEW_TO_OLD)

    @Before
    fun before() {
        feedRepository.makeAction(postRequest)
        authStateRepository.makeAction(authStateRepository.authRequest)
    }

    @Test
    fun test1() = runBlocking {
        val observer = Observer<Any> {}
        val mediator = MediatorLiveData<Any>()
        var feed: FeedEntity<PostEntity>? = null
        voteUseCase.subscribe()

        mediator.observeForever(observer)
        mediator.addSource(feedRepository.getAsLiveData(postRequest)) {
            feed = it
        }

        mediator.addSource(authStateRepository.getAsLiveData(authStateRepository.authRequest)) {
            if (it?.isUserLoggedIn == true && feed?.discussions?.isEmpty() == false) {
                launch {
                    val posts = feed!!.discussions
                    val firstPost = postEntityToModelMapper(posts.first())

                    voteUseCase.vote(
                        VoteRequestModel.VoteForPostRequest(
                            (Math.random() * 10_000).toShort(),
                            firstPost.contentId
                        )
                    )
                }
            }
        }

        var map: Map<VoteRequestModel, QueryResult<VoteRequestModel>>? = null

        mediator.addSource(voteUseCase.getAsLiveData) {
            map = it
            println(map)
        }

        while (map?.entries?.firstOrNull() !is QueryResult.Success<*>){
            delay(300)
        }
    }
}