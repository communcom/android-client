package io.golos.data.api.events

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.EventType
import io.golos.commun4j.services.model.EventsData
import io.golos.commun4j.services.model.FreshResult
import io.golos.commun4j.services.model.ResultOk
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class EventsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), EventsApi {

    override fun getEvents(
        userProfile: String,
        afterId: String?,
        limit: Int?,
        markAsViewed: Boolean?,
        freshOnly: Boolean?,
        types: List<EventType>
    ): EventsData?{
        //commun4j.getEvents(userProfile, afterId, limit, markAsViewed, freshOnly, types).getOrThrow()
        return null
    }

    override fun markEventsAsNotFresh(ids: List<String>): ResultOk {
        //commun4j.markEventsAsNotFresh(ids).getOrThrow()
        return ResultOk("")
    }

    override fun markAllEventsAsNotFresh(): ResultOk {
        //commun4j.markAllEventsAsNotFresh().getOrThrow()
        return ResultOk("")
    }

    override fun getFreshNotifsCount(profileId: String): FreshResult {
        //commun4j.getFreshNotificationCount(profileId).getOrThrow()
        return FreshResult(0)
    }
}