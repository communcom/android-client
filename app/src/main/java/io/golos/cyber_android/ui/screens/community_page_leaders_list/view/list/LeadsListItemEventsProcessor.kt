package io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list

import io.golos.domain.dto.UserIdDomain

interface LeadsListItemEventsProcessor {
    fun retry()

    fun vote(leader: UserIdDomain)

    fun unvote(leader: UserIdDomain)
}