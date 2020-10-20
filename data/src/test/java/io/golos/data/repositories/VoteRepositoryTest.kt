package io.golos.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber4j.Cyber4J
import io.golos.data.api.Commun4jApiBase
import io.golos.data.dispatchersProvider
import io.golos.data.logger
import io.golos.data.repositories.vote.live_data.VoteRepositoryLiveData
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class VoteRepositoryTest {

    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private val cyber4JApi = Commun4jApiBase(Cyber4J())
    private val authStateRepository =
        VoteRepositoryLiveData(cyber4JApi, dispatchersProvider, logger)

    @Test
    fun testVote() {

    }
}