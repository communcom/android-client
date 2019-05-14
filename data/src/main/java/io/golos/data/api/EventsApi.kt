package io.golos.data.api

import io.golos.cyber4j.services.model.EventType
import io.golos.cyber4j.services.model.EventsData
import io.golos.cyber4j.services.model.FreshResult
import io.golos.cyber4j.services.model.ResultOk

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
interface EventsApi {
    fun getEvents(
        userProfile: String,
        afterId: String?,
        limit: Int?, markAsViewed: Boolean?,
        freshOnly: Boolean?, types: List<EventType>
    ): EventsData

    fun markEventsAsNotFresh(ids: List<String>): ResultOk

    fun markAllEventsAsNotFresh(): ResultOk

    fun getFreshNotifsCount(profileId: String): FreshResult
}