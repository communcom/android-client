package io.golos.data.api.events

import io.golos.commun4j.services.model.EventType
import io.golos.commun4j.services.model.EventsData
import io.golos.commun4j.services.model.FreshResult
import io.golos.commun4j.services.model.ResultOk

interface EventsApi {
    fun getEvents(
        userProfile: String,
        afterId: String?,
        limit: Int?, markAsViewed: Boolean?,
        freshOnly: Boolean?, types: List<EventType>
    ): EventsData?

    fun markEventsAsNotFresh(ids: List<String>): ResultOk

    fun markAllEventsAsNotFresh(): ResultOk

    fun getFreshNotifsCount(profileId: String): FreshResult
}