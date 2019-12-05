package io.golos.domain.use_cases.notifs.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.EventTypeEntity
import io.golos.domain.dto.EventsListEntity
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.extensions.map
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.UpdateOption
import io.golos.domain.mappers.EventEntityToModelMapper
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.EventsFeedUpdateRequest
import io.golos.domain.requestmodel.EventsListModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
class EventsUseCase
@Inject
constructor(
    private val eventsRepository: Repository<EventsListEntity, EventsFeedUpdateRequest>,
    private val authRepository: AuthStateRepository,
    private val eventsMapper: EventEntityToModelMapper,
    private val dispatchersProvider: DispatchersProvider
) : UseCase<EventsListModel> {

    private val eventsLiveData = MutableLiveData<EventsListModel>()
    private val freshLiveData = MutableLiveData<Int>()
    private val readinessLiveData = MutableLiveData<Boolean>(false)

    private val eventTypes: Set<EventTypeEntity> = EventTypeEntity.values().toSet()

    private val eventsUpdateStateLiveData = MutableLiveData<QueryResult<UpdateOption>>()

    private val mediatorLiveData = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    private val useCaseScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private var lastEventsJob: Job? = null
    private var isSubscribedOnEvents = false

    override val getAsLiveData: LiveData<EventsListModel>
        get() = eventsLiveData

    val getUnreadLiveData: LiveData<Int> = freshLiveData

    val getReadinessLiveData: LiveData<Boolean> = readinessLiveData

    val getUpdatingState: LiveData<QueryResult<UpdateOption>> = eventsUpdateStateLiveData

    private val lastFetchedChunkLiveData = MutableLiveData<EventsListModel>()

    val getLastFetchedChunk: LiveData<EventsListModel> = lastFetchedChunkLiveData.distinctUntilChanged()

    override fun subscribe() {
        super.subscribe()
        mediatorLiveData.observeForever(observer)
        mediatorLiveData.addSource(authRepository.getAsLiveData(authRepository.allDataRequest)) {
            readinessLiveData.value = it?.isUserLoggedIn ?: false
            val authedUserName = it?.user
            if (it?.isUserLoggedIn == true && !isSubscribedOnEvents && authedUserName != null) {
                isSubscribedOnEvents = true
                val baseRequest = EventsFeedUpdateRequest(
                    CyberName(authedUserName.userId),
                    eventTypes, 0
                )

                mediatorLiveData.addSource(
                    eventsRepository.getAsLiveData(baseRequest)
                ) {
                    lastEventsJob?.cancel()
                    lastEventsJob = useCaseScope.launch {
                        val eventsList = it ?: return@launch
                        freshLiveData.value = eventsList.freshCount
                        val newEvents = withContext(dispatchersProvider.calculationsDispatcher) { eventsMapper.map(eventsList) }
                        lastFetchedChunkLiveData.value = if (eventsLiveData.value == null)
                            newEvents
                        else
                            EventsListModel(newEvents.data - (eventsLiveData.value?.data ?: emptyList()))
                        eventsLiveData.value =
                            newEvents
                    }
                }

                mediatorLiveData.addSource(eventsRepository.updateStates) {
                    it ?: return@addSource
                    val myState = it[baseRequest.id] ?: return@addSource
                    useCaseScope.launch {
                        delay(100)
                        eventsUpdateStateLiveData.value = myState.map(
                            when (myState.originalQuery.lastEventId) {
                                null -> UpdateOption.REFRESH_FROM_BEGINNING
                                else -> UpdateOption.FETCH_NEXT_PAGE
                            }
                        )
                    }
                }
            }
        }

    }

    fun requestUpdate(
        limit: Int,
        option: UpdateOption
    ) {
        if (authRepository.getAsLiveData(authRepository.allDataRequest).value?.isUserLoggedIn != true) {
            println("auth needed for work with this use case")
            return
        }
        val authedUserName = authRepository.getAsLiveData(authRepository.allDataRequest).value?.user
        if (authedUserName == null) {
            println("for some reason authenticated username is null. This should not happen")
            return
        }
        if (option == UpdateOption.REFRESH_FROM_BEGINNING || eventsLiveData.value.isNullOrEmpty()) {
            eventsRepository.makeAction(
                EventsFeedUpdateRequest(
                    CyberName(authedUserName.userId),
                    eventTypes,
                    limit
                )
            )
        } else {
            val lastId = eventsLiveData.value.orEmpty().lastOrNull()?.eventId
            eventsRepository.makeAction(
                EventsFeedUpdateRequest(
                    CyberName(authedUserName.userId),
                    eventTypes,
                    limit,
                    lastId
                )
            )
        }

    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediatorLiveData.removeObserver(observer)
        mediatorLiveData.removeSource(authRepository.getAsLiveData(authRepository.allDataRequest))
        mediatorLiveData.removeSource(eventsRepository.updateStates)
        isSubscribedOnEvents = false
        val authedUserName = authRepository.getAsLiveData(authRepository.allDataRequest).value?.user
        if (authedUserName != null) {
            mediatorLiveData.removeSource(
                eventsRepository.getAsLiveData(
                    EventsFeedUpdateRequest(
                        CyberName(authedUserName.userId),
                        eventTypes, 0
                    )
                )
            )
        }
    }
}