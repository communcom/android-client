package io.golos.cyber_android.ui.common.widgets.post

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.utils.toTimeEstimateFormat
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.extensions.appendSpannedText
import kotlinx.android.synthetic.main.view_post_viewer_header.view.*

/**
 * Header with post info
 */
class PostPageHeaderWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onJoinToCommunityButtonClickListener: (() -> Unit)? = null
    private var onMenuButtonClickListener: (() -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null   // UserId as param

    private lateinit var userId: String

    init {
        inflate(getContext(), R.layout.view_post_viewer_header, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        joinToCommunityButton.setOnClickListener { onJoinToCommunityButtonClickListener?.invoke() }
        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }
    }

    fun setHeader(postHeader: PostHeader) {
        userId = postHeader.userId

        communityTitle.text = postHeader.communityName

        authorAndTime.text = getTimeAndAuthor(postHeader)

        menuButton.isEnabled = postHeader.canEdit

        postHeader.communityAvatarUrl
            ?.let {
                Glide.with(this)
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .into(communityAvatar)
            }

        communityAvatar.setOnClickListener { onUserClickListener?.invoke(userId) }
        communityTitle.setOnClickListener { onUserClickListener?.invoke(userId) }
        authorAndTime.setOnClickListener { onUserClickListener?.invoke(userId) }
        if (postHeader.isJoinFeatureEnabled) {
            joinToCommunityButton.visibility = View.VISIBLE
            joinToCommunityButton.isEnabled = postHeader.canJoinToCommunity
            joinToCommunityButton.isChecked = postHeader.isJoinedToCommunity
        } else {
            joinToCommunityButton.visibility = View.GONE
        }
        if (postHeader.isBackFeatureEnabled) {
            backButton.visibility = View.VISIBLE
        } else {
            backButton.visibility = View.GONE
        }
    }

    fun hideActionMenu() {
        menuButton.visibility = View.INVISIBLE
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnJoinToCommunityButtonClickListener(listener: (() -> Unit)?) {
        onJoinToCommunityButtonClickListener = listener
    }

    fun setOnMenuButtonClickListener(listener: (() -> Unit)?) {
        onMenuButtonClickListener = listener
    }

    /**
     * @param listener - userId as param
     */
    fun setOnUserClickListener(listener: ((String) -> Unit)?) {
        onUserClickListener = listener
    }

    fun release(){
        setOnUserClickListener(null)
        setOnMenuButtonClickListener(null)
        setOnJoinToCommunityButtonClickListener(null)
        setOnBackButtonClickListener(null)
        Glide.with(this)
            .clear(communityAvatar)
    }

    private fun getTimeAndAuthor(postHeader: PostHeader): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        val time = postHeader.actionDateTime.toTimeEstimateFormat(context)
        result.append(time)

        result.append(" ${SpecialChars.bullet} ")
        postHeader.userName?.let {
            result.appendSpannedText(it, ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)))
        }
        return result
    }
}
