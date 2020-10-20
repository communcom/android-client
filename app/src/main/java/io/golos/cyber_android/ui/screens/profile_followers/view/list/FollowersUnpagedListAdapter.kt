package io.golos.cyber_android.ui.screens.profile_followers.view.list

import io.golos.cyber_android.ui.dto.FollowersFilter

open class FollowersUnpagedListAdapter(
    listItemEventsProcessor: FollowersListItemEventsProcessor
) : FollowersPagedListAdapter(listItemEventsProcessor, null, FollowersFilter.MUTUALS) {
    override fun onNextPageReached() {}
}