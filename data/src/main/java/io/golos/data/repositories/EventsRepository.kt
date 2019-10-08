package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.services.model.EventType
import io.golos.data.api.events.EventsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.EventTypeEntity
import io.golos.domain.entities.EventsListDataWithQuery
import io.golos.domain.entities.EventsListEntity
import io.golos.domain.requestmodel.EventsFeedUpdateRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.mappers.EventsToEntityMapper
import io.golos.domain.rules.EntityMerger
import io.golos.domain.rules.RequestApprover
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
class EventsRepository
@Inject
constructor(
    private val eventsApi: EventsApi,
    private val eventsFeedMapper: EventsToEntityMapper,
    private val eventsFeeMerger: EntityMerger<EventsListEntity>,
    private val requestAproover: RequestApprover<EventsFeedUpdateRequest>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : Repository<EventsListEntity, EventsFeedUpdateRequest> {

    private val eventsFeedMap: MutableMap<Identifiable.Id, MutableLiveData<EventsListEntity>> = hashMapOf()

    private val eventsUpdatingStatesMap: MutableLiveData<Map<Identifiable.Id, QueryResult<EventsFeedUpdateRequest>>> =
        MutableLiveData()

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val eventsJobMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    override val allDataRequest: EventsFeedUpdateRequest
        get() = throw UnsupportedOperationException()

    override fun getAsLiveData(params: EventsFeedUpdateRequest): LiveData<EventsListEntity> {
        return eventsFeedMap.getOrPut(params.id) { MutableLiveData() }
    }

    override fun makeAction(params: EventsFeedUpdateRequest) {
        repositoryScope.launch {
            try {
                if (!requestAproover.approve(params)) return@launch

                getAsLiveData(params)

                eventsUpdatingStatesMap.value =
                    eventsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Loading(params))

                val newEvents = withContext(dispatchersProvider.calculationsDispatcher) {
                    eventsApi.getEvents(
                        params.user.name,
                        params.lastEventId,
                        params.limit,
                        true,
                        false,
                        params.types.map { it.toEventType() }
                    ).let { eventsData ->
                        eventsFeedMapper.map(EventsListDataWithQuery(eventsData, params))
                    }
                }
                val oldEvents = getAsLiveData(params).value ?: EventsListEntity(0, 0, null, emptyList())
                val resultEvents =
                    withContext(dispatchersProvider.calculationsDispatcher) { eventsFeeMerger(newEvents, oldEvents) }

                (getAsLiveData(params) as MutableLiveData).value = resultEvents

                eventsUpdatingStatesMap.value =
                    eventsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Success(params))
            } catch (e: Exception) {
                logger.log(e)
                eventsUpdatingStatesMap.value =
                    eventsUpdatingStatesMap.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }
        }.let { job ->
            eventsJobMap[params.id]?.cancel()
            eventsJobMap[params.id] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<EventsFeedUpdateRequest>>>
        get() = eventsUpdatingStatesMap

    private fun EventTypeEntity.toEventType() = when (this) {
        EventTypeEntity.VOTE -> EventType.VOTE
        EventTypeEntity.TRANSFER -> EventType.TRANSFER
        EventTypeEntity.REPLY -> EventType.REPLY
        EventTypeEntity.FLAG -> EventType.FLAG
        EventTypeEntity.SUBSCRIBE -> EventType.SUBSCRIBE
        EventTypeEntity.UN_SUBSCRIBE -> EventType.UN_SUBSCRIBE
        EventTypeEntity.MENTION -> EventType.MENTION
        EventTypeEntity.REPOST -> EventType.REPOST
        EventTypeEntity.REWARD -> EventType.REWARD
        EventTypeEntity.CURATOR_REWARD -> EventType.CURATOR_REWARD
        EventTypeEntity.WITNESS_VOTE -> EventType.WITNESS_VOTE
        EventTypeEntity.WITNESS_CANCEL_VOTE -> EventType.WITNESS_CANCEL_VOTE
    }
}