package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.authStateRepository
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.eventsRepos
import io.golos.domain.entities.EventTypeEntity
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.rules.EventEntityToModelMapper
import junit.framework.Assert.*
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
        var authResult = authStateRepository.updateStates.value
            ?.values?.find {
            it is QueryResult.Loading
        }
        authStateRepository.updateStates.observeForever {
            authResult = it?.values?.find { it is QueryResult.Loading }
        }

        assertEquals(false, readiness)
        assertTrue(authResult is QueryResult.Loading)
        while (authResult is QueryResult.Loading) delay(100)
        assertEquals(true, readiness)
        useCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        assertTrue(updatingState is QueryResult.Loading)
        while (updatingState is QueryResult.Loading) delay(100)

        assertNotNull(events)
        assertEquals(20, events?.data!!.size)

        useCase.requestUpdate(20, UpdateOption.FETCH_NEXT_PAGE)
        assertTrue(updatingState is QueryResult.Loading)

        while (updatingState is QueryResult.Loading) delay(100)
        assertTrue(events?.data!!.size == 40)

        useCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
        assertTrue(updatingState is QueryResult.Loading)

        while (updatingState is QueryResult.Loading) delay(100)
        assertTrue(events?.data!!.size == 20)

        assertTrue(unreads!! > -1)
    }
}