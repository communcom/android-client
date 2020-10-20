package io.golos.domain.rules

import io.golos.domain.dto.EventsListEntity
import io.golos.domain.requestmodel.EventsFeedUpdateRequest
import javax.inject.Inject

class EventsEntityMerger
@Inject
constructor() : EntityMerger<EventsListEntity> {
    override fun invoke(new: EventsListEntity, old: EventsListEntity): EventsListEntity {
        if (old.isEmpty()) return new
        if (new.queryLastItemId == null) return new
        return EventsListEntity(new.total, new.freshCount, new.queryLastItemId, old.data + new.data)
    }
}

class EventsApprover
@Inject
constructor() : RequestApprover<EventsFeedUpdateRequest> {
    override fun approve(param: EventsFeedUpdateRequest): Boolean {
        return true
    }
}