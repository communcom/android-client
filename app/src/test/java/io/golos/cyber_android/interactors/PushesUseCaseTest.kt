package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.appCore
import io.golos.cyber_android.authStateRepository
import io.golos.cyber_android.persister
import io.golos.cyber_android.pushNotifsRepository
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCaseImpl
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.domain.requestmodel.QueryResult
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PushesUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var useCase: PushNotificationsSettingsUseCase

    @Before
    fun before() {
        appCore.initialize()
        useCase = PushNotificationsSettingsUseCaseImpl(pushNotifsRepository, authStateRepository, persister)
    }

    @Test
    fun test() = runBlocking {
        useCase.subscribe()
        useCase.unsubscribe()
        useCase.subscribe()

        var isReady = false
        var updateResult: QueryResult<PushNotificationsStateModel> = QueryResult.Loading(PushNotificationsStateModel.DEFAULT)

        useCase.getReadinessLiveData.observeForever {
            isReady = it
        }

        useCase.getAsLiveData.observeForever {
            updateResult = it
        }

        while (!isReady) delay(1_000)
        assertTrue(updateResult is QueryResult.Success)

        useCase.subscribeToPushNotifications()
        delay(2_000)
        assertTrue(updateResult is QueryResult.Success)

        useCase.unsubscribeFromPushNotifications()
        delay(2_000)
        assertTrue(updateResult is QueryResult.Success)
    }

}