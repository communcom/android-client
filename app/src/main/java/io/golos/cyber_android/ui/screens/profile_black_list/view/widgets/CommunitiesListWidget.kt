package io.golos.cyber_android.ui.screens.profile_black_list.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.widgets.lists.RoundCornersListWidget
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.BlackListListItemEventsProcessor
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.communities.CommunitiesListAdapter

open class CommunitiesListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundCornersListWidget(context, attrs, defStyleAttr) {

    protected var pageSize = 0
    protected lateinit var listItemEventsProcessor: BlackListListItemEventsProcessor

    override fun getAdapter() : VersionedListAdapterBase<*> = CommunitiesListAdapter(listItemEventsProcessor, pageSize)

    fun setAdapterData(pageSize: Int, listItemEventsProcessor: BlackListListItemEventsProcessor) {
        this.pageSize = pageSize
        this.listItemEventsProcessor = listItemEventsProcessor
    }
}