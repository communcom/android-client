package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.apiService
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.logger
import io.golos.data.repositories.AuthStateRepository
import io.golos.domain.Persister
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.UserAuthState
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.model.AuthRequestModel
import io.golos.domain.model.QueryResult
import io.golos.domain.model.SignInState
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

    private lateinit var authUseCase: SignInUseCase
    private lateinit var authStateRepository: AuthStateRepository

    @Before
    fun before() {

        authStateRepository = AuthStateRepository(apiService, dispatchersProvider, logger, object : Persister {
            override fun saveAuthState(state: AuthState) {

            }

            override fun getAuthState(): AuthState? {
                return null
            }

            override fun saveActiveKey(activeKey: String) {

            }

            override fun getActiveKey(): String? {
                return null
            }
        })
        authUseCase = SignInUseCase(authStateRepository, dispatchersProvider)
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

        var signInState: SignInState? = null
        authUseCase.getSignInState.observeForever {
            signInState = it
        }

        delay(100)

        assertEquals(SignInState.LOG_IN_NEEDED, signInState)

        authUseCase.authWithCredentials(AuthRequestModel(CyberUser("sdgsdgsg"), "ssdgsd gsdgsdg3essd"))


        assertTrue(authResult!!.values.first() is QueryResult.Error)
        assertEquals(SignInState.LOG_IN_NEEDED, signInState)

        authUseCase.authWithCredentials(AuthRequestModel(CyberUser("sdgsdgsg"), "ssdgsd gsd235235gsdg3essd"))

        assertTrue(authResult!!.values.size == 1)
        assertTrue(authResult!!.values.first() is QueryResult.Error)
        assertEquals(SignInState.LOG_IN_NEEDED, signInState)

        authUseCase.authWithCredentials(
            AuthRequestModel(
                CyberUser("fkmiiibuntct"),
                "5JyzKR94WqFxqcExLMcakd7SksEqkNDbo2GT4VvdRs4g3XyQrg8"
            )
        )

        assertTrue(authResult!!.values.size == 2)
        assertTrue(authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Loading)
        assertEquals(SignInState.LOADING, signInState)


        while (authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Loading) delay(200)

        assertTrue(authSate!!.isUserLoggedIn)
        assertEquals(CyberName("fkmiiibuntct"), authSate!!.userName)
        assertTrue(authResult!![CyberUser("fkmiiibuntct")] is QueryResult.Success)
        assertEquals(SignInState.USER_LOGGED_IN, signInState)

    }
}