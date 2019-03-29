package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.authStateRepository
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.UserAuthState
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.model.AuthRequestModel
import io.golos.domain.model.QueryResult
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
class AuthUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    lateinit var authUseCase: SignInUseCase

    @Before
    fun before() {
        authUseCase = SignInUseCase(authStateRepository)
    }

    @Test
    fun test() = runBlocking {
        authUseCase.subscribe()

        var authSate: UserAuthState? = null
        authUseCase.getAsLiveData.observeForever {
            println(it)
            authSate = it
        }

        var authResult: Map<CyberUser, QueryResult<AuthRequestModel>>? = null
        authUseCase.getLogInStates.observeForever {
            println(it)
            authResult = it
        }

        authUseCase.authWithCredentials(AuthRequestModel(CyberUser("sdgsdgsg"), "ssdgsd gsdgsdg3essd"))

        assertTrue(authResult!!.values.first() is QueryResult.Error)


        authUseCase.authWithCredentials(AuthRequestModel(CyberUser("sdgsdgsg"), "ssdgsd gsd235235gsdg3essd"))

        assertTrue(authResult!!.values.size == 1)
        assertTrue(authResult!!.values.first() is QueryResult.Error)

        authUseCase.authWithCredentials(
            AuthRequestModel(
                CyberUser("fkmiiibuntct"),
                "5JyzKR94WqFxqcExLMcakd7SksEqkNDbo2GT4VvdRs4g3XyQrg8"
            )
        )

        assertTrue(authResult!!.values.size == 2)
        assertTrue(authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Loading)


        while (authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Loading) delay(200)

        assertTrue(authSate!!.isUserLoggedIn)
        assertEquals(CyberName("fkmiiibuntct"), authSate!!.userName)
        assertTrue(authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Success)

    }
}