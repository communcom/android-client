package io.golos.cyber_android.ui.shared.widgets.post_comments.voting

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.shared.widgets.post_comments.donation.VotingDonatePopup
import kotlinx.android.synthetic.main.view_post_voting.view.*

class VotingWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var onUpVoteButtonClickListener: (() -> Unit)? = null
    private var onDownVoteButtonClickListener: (() -> Unit)? = null
    private var onDonateClickListener: ((DonateType) -> Unit)? = null

    private val donatePopup: VotingDonatePopup by lazy { VotingDonatePopup() }

    init {
        inflate(getContext(), R.layout.view_post_voting, this)

        upvoteButton.setOnClickListener {
            onUpVoteButtonClickListener?.invoke()
            onDonateClickListener?.let { donatePopup.show(this, it) }
        }

        downvoteButton.setOnClickListener { onDownVoteButtonClickListener?.invoke() }
    }

    fun setOnUpVoteButtonClickListener(listener: (() -> Unit)?) {
        onUpVoteButtonClickListener = listener
    }

    fun setOnDownVoteButtonClickListener(listener: (() -> Unit)?) {
        onDownVoteButtonClickListener = listener
    }

    fun setOnDonateClickListener(listener: ((DonateType) -> Unit)?) {
        onDonateClickListener = listener
    }

    fun setUpVoteButtonSelected(isSelected: Boolean) {
        upvoteButton.isSelected = isSelected
    }

    fun setDownVoteButtonSelected(isSelected: Boolean) {
        downvoteButton.isSelected = isSelected
    }

    fun setVoteBalance(balance: Long) {
        votesText.text = balance.toString()
    }

    fun release() {
        onUpVoteButtonClickListener = null
        onDownVoteButtonClickListener = null
        onDonateClickListener = null
    }
}
