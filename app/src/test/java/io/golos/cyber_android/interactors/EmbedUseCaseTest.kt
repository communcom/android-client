package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.embedsRepository
import io.golos.domain.interactors.model.ProccesedLinksModel
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.model.QueryResult
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
class EmbedUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var embedUseCase: EmbedsUseCase


    @Before
    fun before() {
        embedUseCase = EmbedsUseCase(
            dispatchersProvider,
            embedsRepository
        )
    }

    @Test
    fun testSomeEmbeds() = runBlocking {
        val firstLink = "https://github.com/square/moshi"
        val secondLink  = "https://www.reddit.com/r/aww/comments/b80omf/youve_got_april_fooled_bro/"

        embedUseCase.subscribe()
        embedUseCase.unsubscribe()
        embedUseCase.subscribe()

        var processedLinksModel: ProccesedLinksModel? = null
        embedUseCase.getAsLiveData.observeForever {
            processedLinksModel = it
        }
        embedUseCase.requestLinkEmbedData(firstLink)



        assertEquals(1, processedLinksModel!!.size)
        assertTrue(processedLinksModel!!.getValue(firstLink) is QueryResult.Loading)

        while (processedLinksModel!!.getValue(firstLink) is QueryResult.Loading) delay(200)

        assertTrue(processedLinksModel!!.getValue(firstLink) is QueryResult.Success)


        embedUseCase.requestLinkEmbedData(secondLink)

        assertEquals(2, processedLinksModel!!.size)
        assertTrue(processedLinksModel!!.getValue(firstLink) is QueryResult.Success)
        assertTrue(processedLinksModel!!.getValue(secondLink) is QueryResult.Loading)

        while (processedLinksModel!!.getValue(secondLink) is QueryResult.Loading) delay(200)

        assertTrue(processedLinksModel!!.getValue(firstLink) is QueryResult.Success)
        assertTrue(processedLinksModel!!.getValue(secondLink) is QueryResult.Success)

    }
}