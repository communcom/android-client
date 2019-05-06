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
import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.SignInState
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
class SignInUseCaseTest {
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
            authSate = it
        }

        var authResult: Map<CyberUser, QueryResult<AuthRequestModel>>? = null
        authUseCase.getLogInStates.observeForever {
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
                CyberUser("anpacifgrlqe"),
                "5JB6WdGo7tvArMP6u3FtwfYGzBei8wMEyaVyrACkczGrbA6BviF"
            )
        )

        assertTrue(authResult!!.values.size == 2)
        assertTrue(authResult!![CyberUser("anpacifgrlqe")] is QueryResult.Loading)
        assertEquals(SignInState.LOADING, signInState)


        while (authResult!![CyberUser("anpacifgrlqe")] is QueryResult.Loading) delay(200)

        assertTrue(authSate!!.isUserLoggedIn)
        assertEquals(CyberName("anpacifgrlqe"), authSate!!.userName)
        assertTrue(authResult!![CyberUser("anpacifgrlqe")] is QueryResult.Success)
        assertEquals(SignInState.USER_LOGGED_IN, signInState)

    }
}