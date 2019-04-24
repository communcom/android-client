package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.authStateRepository
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.eventsRepos
import io.golos.domain.entities.EventTypeEntity
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.rules.EventEntityToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
class EventsUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var useCase: EventsUseCase

    @Before
    fun before() {
        useCase = EventsUseCase(
            EventTypeEntity.values().toSet(),
            eventsRepos,
            authStateRepository,
            EventEntityToModelMapper(),
            dispatchersProvider
        )
    }

    @Test
    fun testEvents() = runBlocking {
        useCase.subscribe()
        useCase.unsubscribe()
        useCase.subscribe()

        var unreads = useCase.getUnreadLiveData.value
        useCase.getUnreadLiveData.observeForever {
            unreads = it
        }
        var events = useCase.getAsLiveData.value
        useCase.getAsLiveData.observeForever {
            events = it
        }
        var updatingState = useCase.getUpdatingState.value
        useCase.getUpdatingState.observeForever {
            updatingState = it
        }
        var readiness = useCase.getReadinessLiveData.value
        useCase.getReadinessLiveData.observeForever {
            readiness = it
        }

        delay(20_000)
    }
}