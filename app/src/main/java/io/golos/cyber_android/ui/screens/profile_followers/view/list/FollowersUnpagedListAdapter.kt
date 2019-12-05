package io.golos.cyber_android.ui.screens.profile_followers.view.list

open class FollowersUnpagedListAdapter(
    listItemEventsProcessor: FollowersListItemEventsProcessor
) : FollowersPagedListAdapter(listItemEventsProcessor, null) {
    override fun onNextPageReached() {}
}