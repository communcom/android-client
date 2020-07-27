package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.post_page_menu.model.ShowInExplorerListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_post_menu.view.*

class ShowInExplorerItem(
    private val parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, ShowInExplorerListItem>(
    parentView,
    R.layout.item_post_menu){
    override fun init(listItem: ShowInExplorerListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        itemView.menuAction.text = parentView.context.resources.getText(R.string.view_in_explorer)
        itemView.menuAction.setOnClickListener { listItemEventsProcessor.onViewInExplorerClick() }
    }

}