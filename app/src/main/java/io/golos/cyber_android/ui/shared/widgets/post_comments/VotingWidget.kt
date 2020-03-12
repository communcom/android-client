package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import io.golos.cyber_android.R
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

    init {
        inflate(getContext(), R.layout.view_post_voting, this)

        upvoteButton.setOnClickListener { onUpVoteButtonClickListener?.invoke() }
        downvoteButton.setOnClickListener { onDownVoteButtonClickListener?.invoke() }
    }

    fun setOnUpVoteButtonClickListener(listener: (() -> Unit)?) {
        onUpVoteButtonClickListener = listener
    }

    fun setOnDownVoteButtonClickListener(listener: (() -> Unit)?) {
        onDownVoteButtonClickListener = listener
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
    }
}
