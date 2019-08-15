package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * [onScrollListener] argument is last visible item index
 */
class CommunityListScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val onScrollListener: (Int) -> Unit
) : RecyclerView.OnScrollListener() {

    private var priorPosition = Int.MIN_VALUE

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    {
        // to avoid empty firing
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if(priorPosition!=lastVisibleItemPosition)
        {
            priorPosition = lastVisibleItemPosition
            onScrollListener(lastVisibleItemPosition)
        }
    }
}