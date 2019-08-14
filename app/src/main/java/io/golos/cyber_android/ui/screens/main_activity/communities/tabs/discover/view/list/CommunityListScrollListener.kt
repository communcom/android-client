package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CommunityListScrollListener(
//    private val presenter: ChatRoomsListPresenterInterface,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private var priorPosition = Int.MIN_VALUE

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    {
        // to avoid empty firing
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if(priorPosition!=lastVisibleItemPosition)
        {
            priorPosition = lastVisibleItemPosition
            call view model here
            clone list in the model by calling toList()
  //          presenter.onScrollChatRoomsList(lastVisibleItemPosition)
        }
    }
}