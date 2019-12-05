package io.golos.cyber_android.ui.screens.profile_followers.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersPagedListAdapter

class RoundCornersPagedList
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersList(context, attrs, defStyleAttr) {

    private var pageSize = 0
    private lateinit var listItemEventsProcessor: FollowersListItemEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> =
        FollowersPagedListAdapter(listItemEventsProcessor, pageSize)

    fun setAdapterData(pageSize: Int, listItemEventsProcessor: FollowersListItemEventsProcessor) {
        this.pageSize = pageSize
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}