package io.golos.cyber_android.ui.screens.profile_followers.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersPagedListAdapter
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.widgets.lists.RoundCornersListWidget

class RoundCornersPagedListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersListWidget(context, attrs, defStyleAttr) {

    private var pageSize = 0
    private lateinit var filter: FollowersFilter
    private lateinit var listItemEventsProcessor: FollowersListItemEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> =
        FollowersPagedListAdapter(listItemEventsProcessor, pageSize, filter)

    fun setAdapterData(pageSize: Int, filter: FollowersFilter, listItemEventsProcessor: FollowersListItemEventsProcessor) {
        this.pageSize = pageSize
        this.filter = filter
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}