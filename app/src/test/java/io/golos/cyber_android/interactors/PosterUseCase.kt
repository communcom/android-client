package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.discussionCreationRepo
import io.golos.cyber_android.dispatchersProvider
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.model.QueryResult
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class PosterUseCase {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var useCase: DiscussionPosterUseCase

    @Before
    fun before() {
        useCase = DiscussionPosterUseCase(discussionCreationRepo, dispatchersProvider)
    }

    @Test
    fun postTest() = runBlocking {
        useCase.subscribe()
        useCase.unsubscribe()
        useCase.subscribe()

        var disscussionResult: QueryResult<DiscussionCreationResultModel>? = null

        useCase.getAsLiveData.observeForever {
            disscussionResult = it
        }

        useCase.createPostOrComment(PostCreationRequestModel("test title", "супер тело", listOf("nsfw")))

        assertTrue(disscussionResult is QueryResult.Loading)

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        useCase.createPostOrComment(PostCreationRequestModel("test title", "хава нагила", listOf("nsfw")))

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        val discussionCreateResult = (disscussionResult as QueryResult.Success).originalQuery as PostCreationResultModel

        useCase.createPostOrComment(
            CommentCreationRequestModel(
                "test title",
                discussionCreateResult.postId,
                listOf("nsfw")
            )
        )

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        assertTrue((disscussionResult as QueryResult.Success).originalQuery is CommentCreationResultModel)
    }
}