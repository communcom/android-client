package io.golos.cyber_android.ui.screens.profile_followers.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.widgets.lists.RoundCornersListWidget
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersUnpagedListAdapter

class RoundCornersUnpagedListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersListWidget(context, attrs, defStyleAttr) {

    private lateinit var listItemEventsProcessor: FollowersListItemEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> = FollowersUnpagedListAdapter(listItemEventsProcessor)

    fun setAdapterData(listItemEventsProcessor: FollowersListItemEventsProcessor) {
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}