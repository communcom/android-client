package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.PostControlsListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import kotlinx.android.synthetic.main.item_post_controls.view.*

class PostControlsViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, PostControlsListItem>(
    parentView,
    R.layout.item_post_controls
) {
    override fun init(listItem: PostControlsListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        with(itemView) {
            votesArea.setVoteBalance(listItem.voteBalance)

            val countersFormatter = io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter()

            viewCountsText.text = countersFormatter.format(listItem.totalViews)
            commentsCountText.text = countersFormatter.format(listItem.totalComments)

            votesArea.setUpVoteButtonSelected(listItem.isUpVoteActive)
            votesArea.setDownVoteButtonSelected(listItem.isDownVoteActive)

            votesArea.setOnUpVoteButtonClickListener { listItemEventsProcessor.onUpVoteClick() }
            votesArea.setOnDownVoteButtonClickListener { listItemEventsProcessor.onDownVoteClick() }
        }
    }

    override fun release() {
        itemView.votesArea.setOnUpVoteButtonClickListener(null)
        itemView.votesArea.setOnDownVoteButtonClickListener(null)
    }
}