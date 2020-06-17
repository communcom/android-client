package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import kotlinx.android.synthetic.main.view_post_voting.view.*
import timber.log.Timber

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

        upvoteButton.setOnClickListener {
            showDonate()
            onUpVoteButtonClickListener?.invoke()
        }

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

    private fun showDonate() {
        val balloon = Balloon.Builder(context)
            .setArrowVisible(true)
            .setArrowPosition(0.15f)
            .setLifecycleOwner(parentActivity)
            .setWidthRatio(0.98f)
            .setHeight(100)
            .setPadding(0)
            .setElevation(10f)
            .setAutoDismissDuration(5_000L)
            .setDismissWhenTouchOutside(true)
            .setCornerRadius(11f)
            .setLayout(R.layout.view_donat_popup)
            .build()

        val contentView = balloon.getContentView()
        contentView.setBackgroundResource(R.drawable.bcg_thin_gray_stroke_ripple_6)

        balloon.showAlignTop(this)
    }
}
