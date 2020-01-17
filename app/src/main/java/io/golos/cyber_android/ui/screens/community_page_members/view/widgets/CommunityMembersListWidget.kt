package io.golos.cyber_android.ui.screens.community_page_members.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.screens.community_page_members.view.list.CommunityMembersListAdapter
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.widgets.lists.RoundCornersListWidget

class CommunityMembersListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersListWidget(context, attrs, defStyleAttr) {

    private var pageSize = 0
    private lateinit var listItemEventsProcessor: MembersListEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> = CommunityMembersListAdapter(listItemEventsProcessor, pageSize)

    fun setAdapterData(pageSize: Int, listItemEventsProcessor: MembersListEventsProcessor) {
        this.pageSize = pageSize
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}