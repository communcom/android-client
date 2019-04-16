package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.authStateRepository
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.regRepo
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.model.QueryResult
import junit.framework.Assert.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class SignOnUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    lateinit var case: SignUpUseCase

    @Before
    fun before() {
        case = SignUpUseCase(true,
            regRepo,
            authStateRepository,
            dispatchersProvider,
            object : TestPassProvider {
                override fun provide(): String {
                    return SignOnUseCaseTest::class.java.classLoader.getResource("phone_code.txt").readText()
                }
            })
    }

    @Test
    fun testReg() = runBlocking {
        case.subscribe()
        case.unsubscribe()
        case.subscribe()
        var userAuthState = authStateRepository.getAsLiveData(authStateRepository.allDataRequest).value
        authStateRepository.getAsLiveData(authStateRepository.allDataRequest).observeForever {
            userAuthState = it
        }
        var authProgress = authStateRepository.updateStates.value.orEmpty().values.firstOrNull()
        authStateRepository.updateStates.observeForever {
            authProgress = it.values.orEmpty().find { it is QueryResult.Loading }
        }

        var registrationStep = case.getAsLiveData.value
        var updatingState = case.getUpdatingState.value
        var lastRegisteredUser = case.getLastRegisteredUser.value

        val randomPhone = generateRandomPhone()
        val randomUser = generateRandomCommunName()


        case.getAsLiveData.observeForever {
            registrationStep = it
        }
        case.getUpdatingState.observeForever {
            updatingState = it
        }
        case.getLastRegisteredUser.observeForever {
            lastRegisteredUser = it
        }

        case.makeRegistrationStep(SendSmsForVerificationRequestModel(randomPhone))

        assertTrue(updatingState is QueryResult.Success)

        assertEquals(UnverifiedUserModel::class.java, registrationStep!!.javaClass)

        case.makeRegistrationStep(
            SendVerificationCodeRequestModel(
                randomPhone,
                (registrationStep as UnverifiedUserModel).smsCode!!
            )
        )
        assertTrue(updatingState is QueryResult.Success)

        assertTrue(registrationStep is VerifiedUserWithoutUserNameModel)

        case.makeRegistrationStep(SetUserNameRequestModel(randomPhone, randomUser))
        assertTrue(updatingState is QueryResult.Success)


        assertTrue(registrationStep is UnWrittenToBlockChainUserModel)

        case.makeRegistrationStep(WriteUserToBlockChainRequestModel(randomPhone, randomUser))

        assertTrue(updatingState is QueryResult.Success)


        assertTrue(registrationStep is RegisteredUserModel)
        assertNotNull(lastRegisteredUser)

        assertTrue(authProgress is QueryResult.Loading)
        while (authProgress is QueryResult.Loading) delay(200)

        assertTrue(userAuthState!!.isUserLoggedIn)
    }

    @Test
    fun testSwitchUsers() {
        case.subscribe()
        case.unsubscribe()
        case.subscribe()

        var registrationStep = case.getAsLiveData.value
        var updatingState = case.getUpdatingState.value
        var lastRegisteredUser = case.getLastRegisteredUser.value

        val randomPhone = generateRandomPhone()
        val anotherPhone = generateRandomPhone()
        val randomUser = generateRandomCommunName()


        case.getAsLiveData.observeForever {
            registrationStep = it
        }
        case.getUpdatingState.observeForever {
            updatingState = it
        }
        case.getLastRegisteredUser.observeForever {
            lastRegisteredUser = it
        }

        case.makeRegistrationStep(SendSmsForVerificationRequestModel(randomPhone))

        assertTrue(updatingState is QueryResult.Success)

        assertEquals(UnverifiedUserModel::class.java, registrationStep!!.javaClass)

        //switching

        case.makeRegistrationStep(SendSmsForVerificationRequestModel(anotherPhone))

        assertTrue(updatingState is QueryResult.Success)

        assertEquals(UnverifiedUserModel::class.java, registrationStep!!.javaClass)

        case.makeRegistrationStep(
            SendVerificationCodeRequestModel(
                anotherPhone,
                (registrationStep as UnverifiedUserModel).smsCode!!
            )
        )
        assertTrue(updatingState is QueryResult.Success)

        assertTrue(registrationStep is VerifiedUserWithoutUserNameModel)

        case.makeRegistrationStep(SetUserNameRequestModel(anotherPhone, randomUser))
        assertTrue(updatingState is QueryResult.Success)


        assertTrue(registrationStep is UnWrittenToBlockChainUserModel)

        case.makeRegistrationStep(WriteUserToBlockChainRequestModel(anotherPhone, randomUser))

        assertTrue(updatingState is QueryResult.Success)


        assertTrue(registrationStep is RegisteredUserModel)
        assertNotNull(lastRegisteredUser)


    }

    private fun generateRandomPhone(): String = StringBuilder("+7").let { sb ->
        (0..10).forEach {
            sb.append((Math.random() * 9).toInt())
        }
        sb.toString()
    }

    private fun generateRandomCommunName(): CyberName {
        val builder = StringBuilder()
        (0..11).forEach {
            builder.append((Math.random() * 25).toChar() + 97)
        }
        return builder.toString().toCyberName()
    }
}