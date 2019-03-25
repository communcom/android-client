package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.*
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
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

    private val voteUseCase =
        VoteUseCase(
            authStateRepository, voteRepo,
            dispatchersProvider, voteEntityToPostMapper,
            voteToEntityMapper
        )

    private val postRequest = CommunityFeedUpdateRequest("gls", 1, DiscussionsSort.FROM_NEW_TO_OLD)

    @Before
    fun before() {
        appCore.initialize()
        feedRepository.makeAction(postRequest)
    }

    @Test
    fun test1() = runBlocking {
        val observer = Observer<Any> {}
        val mediator = MediatorLiveData<Any>()
        var feed: FeedEntity<PostEntity>? = null
        voteUseCase.subscribe()
        voteUseCase.unsubscribe()
        voteUseCase.subscribe()

        mediator.observeForever(observer)
        mediator.addSource(feedRepository.getAsLiveData(postRequest)) {
            feed = it
        }

        mediator.addSource(authStateRepository.getAsLiveData(authStateRepository.allDataRequest)) {
            if (it?.isUserLoggedIn == true && feed?.discussions?.isEmpty() == false) {
                val firstPost = feed!!.discussions.first().contentId
                val firstPostId = DiscussionIdModel(firstPost.userId, firstPost.permlink, firstPost.refBlockNum)
                voteUseCase.vote(
                    VoteRequestModel.VoteForPostRequest(
                        (Math.random() * 10_000).toShort(),
                        firstPostId
                    )
                )
            }
        }

        var counter = -1
        mediator.addSource(voteUseCase.getAsLiveData) {
            counter++
            println(it)
            if (counter == 0) {
                return@addSource
            } else {
                val item = it.values.first()

                assertTrue(it.size == 1)

                if (counter % 2 == 0) {
                    assertTrue(item is QueryResult.Loading)
                } else {
                    assertTrue(item is QueryResult.Success)
                }
            }


        }

        delay(3_000)

        assertTrue(feed!!.discussions.first().votes.hasUpVote)


        val firstPost = feed!!.discussions.first().contentId
        val firstPostId = DiscussionIdModel(firstPost.userId, firstPost.permlink, firstPost.refBlockNum)
        voteUseCase.vote(
            VoteRequestModel.VoteForPostRequest(
                (Math.random() * -10_000).toShort(),
                firstPostId
            )
        )

        delay(3_000)

        assertTrue(feed!!.discussions.first().votes.hasDownVote)
    }
}