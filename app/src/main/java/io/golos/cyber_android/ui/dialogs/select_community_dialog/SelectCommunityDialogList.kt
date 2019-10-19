package io.golos.cyber_android.ui.dialogs.select_community_dialog

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListItemEventsProcessor
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListScrollListener
import kotlinx.android.synthetic.main.fragment_communities_select_dialog_list.view.*

@Suppress("unused")
class SelectCommunityDialogList
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var listAdapter: CommunityListAdapter
    private lateinit var listLayoutManager: LinearLayoutManager
    private var scrollListener: CommunityListScrollListener? = null

    /**
     * The argument is position of a last visible item
     */
    private var onScrollListener: ((Int) -> Unit)? = null

    init {
        inflate(context, R.layout.fragment_communities_select_dialog_list, this)
    }

    fun updateList(data: List<ListItem>, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(!::listAdapter.isInitialized) {
            listLayoutManager = LinearLayoutManager(context)

            listAdapter = CommunityListAdapter(listItemEventsProcessor)
            listAdapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = listLayoutManager
            itemsList.adapter = listAdapter
        }

        listAdapter.update(data)
    }

    fun setScrollState(isScrollEnabled: Boolean) {
        if(!::listAdapter.isInitialized) {
            return
        }

        scrollListener?.let { itemsList.removeOnScrollListener(it) }

        if(isScrollEnabled) {
            itemsList.addOnScrollListener(CommunityListScrollListener(listLayoutManager) { onScrollListener?.invoke(it) })
        }
    }

    fun setOnScrollListener(listener: ((Int) -> Unit)?) {
        onScrollListener = listener
    }
}
