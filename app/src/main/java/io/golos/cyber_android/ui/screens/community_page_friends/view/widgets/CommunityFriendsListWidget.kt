package io.golos.cyber_android.ui.screens.community_page_friends.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.screens.community_page_friends.view.list.CommunityFriendsListAdapter
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.widgets.lists.RoundCornersListWidget

class CommunityFriendsListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersListWidget(context, attrs, defStyleAttr) {

    private lateinit var listItemEventsProcessor: MembersListEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> = CommunityFriendsListAdapter(listItemEventsProcessor)

    fun setAdapterData(listItemEventsProcessor: MembersListEventsProcessor) {
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}