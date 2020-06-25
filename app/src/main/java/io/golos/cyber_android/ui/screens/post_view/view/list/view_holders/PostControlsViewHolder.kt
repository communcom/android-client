package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.utils.format.KiloCounterFormatter
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.PostControlsListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.widgets.post_comments.donation.DonatePersonsPopup
import io.golos.utils.helpers.positiveValue
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

            viewCountsText.text = KiloCounterFormatter.format(listItem.totalViews)
            commentsCountText.text = KiloCounterFormatter.format(listItem.totalComments)

            votesArea.setUpVoteButtonSelected(listItem.isUpVoteActive)
            votesArea.setDownVoteButtonSelected(listItem.isDownVoteActive)

            votesArea.setOnUpVoteButtonClickListener { listItemEventsProcessor.onUpVoteClick() }
            votesArea.setOnDownVoteButtonClickListener { listItemEventsProcessor.onDownVoteClick() }
            votesArea.setOnDonateClickListener { listItemEventsProcessor.onDonateClick(it, listItem.post) }

            viewCountText.text = KiloCounterFormatter.format(listItem.post.viewCount)

            if(listItem.post.donation != null) {
                donationPanel.setAmount(listItem.post.donation.totalAmount)
                donationPanel.visibility = View.VISIBLE
                donationPanel.setOnClickListener { DonatePersonsPopup().show(donationPanel, listItem.post.donation) {
                    listItemEventsProcessor.onDonatePopupClick(listItem.post.donation)
                }}
            } else {
                donationPanel.visibility = View.INVISIBLE
            }

            ivShare.setOnClickListener {
                listItem.shareUrl?.let {
                    listItemEventsProcessor.onShareClicked(it)
                }
            }
        }
    }

    override fun release() {
        itemView.votesArea.setOnUpVoteButtonClickListener(null)
        itemView.votesArea.setOnDownVoteButtonClickListener(null)
        itemView.votesArea.setOnDonateClickListener(null)
        itemView.donationPanel.setOnClickListener(null)
    }
}