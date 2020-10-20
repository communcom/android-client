package io.golos.cyber_android.ui.screens.community_page_friends.view.list

import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.screens.community_page_members.view.list.CommunityMembersListAdapter

class CommunityFriendsListAdapter(
    listItemEventsProcessor: MembersListEventsProcessor
) : CommunityMembersListAdapter(listItemEventsProcessor, null) {
    override fun onNextPageReached() {
        // do nothing
    }
}