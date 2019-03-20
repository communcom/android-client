package io.golos.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber4j.Cyber4J

import io.golos.data.api.Cyber4jApiService
import io.golos.data.dispatchersProvider
import io.golos.data.logger
import io.golos.domain.model.QueryResult
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class AuthStateRepositoryTest {

    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private val cyber4JApi = Cyber4jApiService(Cyber4J())
    private val authStateRepository = AuthStateRepository(cyber4JApi, dispatchersProvider, logger)

    @Test
    fun test() {
        authStateRepository.makeAction(authStateRepository.authRequest)
        var authState = authStateRepository.getAuthState.value


        authStateRepository.getAuthState.observeForever() {
            println("$it")
            authState = it
        }
        authStateRepository.updateStates.observeForever {
            println("auth requests = $it")
        }

        Thread.sleep(2_000)
        assertTrue(authState!!.isUserLoggedIn)
        assertTrue(authStateRepository
            .updateStates
            .value
            ?.values
            ?.find { it is QueryResult.Success } != null)

    }
}