package io.golos.cyber_android.ui.screens.community_page_members.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.screens.community_page_members.view.UsersListEventsProcessor
import io.golos.cyber_android.ui.screens.community_page_members.view.list.CommunityUsersListAdapter
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
    private lateinit var listItemEventsProcessor: UsersListEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> = CommunityUsersListAdapter(listItemEventsProcessor, pageSize)

    fun setAdapterData(pageSize: Int, listItemEventsProcessor: UsersListEventsProcessor) {
        this.pageSize = pageSize
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}