package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.RewardInfo
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.utils.format.RewardFormatter
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.cyber_android.ui.shared.utils.toTimeEstimateFormat
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.RewardCurrency
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan
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
    private var onMenuButtonClickListener: (() -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null   // UserId as param
    private var onCommunityClickListener: ((CommunityIdDomain) -> Unit)? = null //CommunityId as param
    private var onRewardClickListener: (() -> Unit)? = null     //CommunityId as param

    private lateinit var userId: String

    init {
        inflate(getContext(), R.layout.view_post_viewer_header, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }

        rewardButton.setOnClickListener {
            onRewardClickListener?.invoke()
        }

        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }
    }

    fun setHeader(postHeader: PostHeader) {
        userId = postHeader.userId
        communityTitle.adjustSpannableClicks()
        val builder = SpannableStringBuilder()

        if(postHeader.communityId!!.code == GlobalConstants.MY_FEED_COMMUNITY_ID) {
            (postHeader.userName ?: postHeader.userId).let {
                val textInterval = builder.appendText(it)
                val communityNameTextColor = ContextCompat.getColor(context, R.color.post_header_community_text)
                builder.setSpan(object : ColorTextClickableSpan(it, communityNameTextColor) {
                    override fun onClick(widget: View) {
                        postHeader.communityId.let { id ->
                            onUserClickListener?.invoke(postHeader.userId)
                        }
                    }

                }, textInterval)
            }
            communityTitle.text = builder
        } else {
            postHeader.communityName?.let {
                val textInterval = builder.appendText(it)
                val communityNameTextColor = ContextCompat.getColor(context, R.color.post_header_community_text)
                builder.setSpan(object : ColorTextClickableSpan(it, communityNameTextColor) {
                    override fun onClick(widget: View) {
                        postHeader.communityId.let { id ->
                            onCommunityClickListener?.invoke(id)
                        }
                    }

                }, textInterval)
            }
            communityTitle.text = builder
        }

        authorAndTime.adjustSpannableClicks()

        authorAndTime.text = getTimeAndAuthor(postHeader)

        if(postHeader.communityId.code == GlobalConstants.MY_FEED_COMMUNITY_ID) {
            communityAvatar.loadAvatar(postHeader.userAvatarUrl)
            communityAvatar.setOnClickListener {
                postHeader.userId.let { id ->
                    onUserClickListener?.invoke(id)
                }
            }
        } else {
            communityAvatar.loadCommunity(postHeader.communityAvatarUrl)
            communityAvatar.setOnClickListener {
                postHeader.communityId.let { id ->
                    onCommunityClickListener?.invoke(id)
                }
            }
        }

        if (postHeader.reward != null) {
            rewardButton.visibility = View.VISIBLE
            rewardButton.text = getRewardAsString(postHeader.reward)
        } else {
            rewardButton.visibility = View.GONE
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

    fun setOnRewardButtonClickListener(listener: (() -> Unit)?) {
        onRewardClickListener = listener
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

    fun setOnCommunityClickListener(listener: ((CommunityIdDomain) -> Unit)?) {
        onCommunityClickListener = listener
    }

    fun release(){
        setOnUserClickListener(null)
        setOnMenuButtonClickListener(null)
        setOnRewardButtonClickListener(null)
        setOnBackButtonClickListener(null)
        communityAvatar.clear()
    }

    private fun getRewardAsString(reward: RewardInfo): String =
        when(reward.rewardCurrency) {
            RewardCurrency.POINTS -> reward.rewardValueInPoints?.let { RewardFormatter.formatPoints(it) }
            RewardCurrency.COMMUNS -> reward.rewardValueInCommun?.let { "${RewardFormatter.formatCommun(it)} ${context.getString(R.string.commun_currency_brief)}" }
            RewardCurrency.USD -> reward.rewardValueInUSD?.let { "$${RewardFormatter.formatUSD(it)}" }
        } ?: context.getString(R.string.post_reward_top)

    private fun getTimeAndAuthor(postHeader: PostHeader): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        val time = postHeader.actionDateTime.toTimeEstimateFormat(context)
        val timeInterval = result.appendText(time)

        val timeColor = ContextCompat.getColor(context, R.color.post_header_time_text)

        result.setSpan(object : ColorTextClickableSpan(time, timeColor) {

            override fun onClick(widget: View) {
                callOnClick()
            }

        }, timeInterval)

        val bulletSymbol = " ${SpecialChars.BULLET} "
        val bulletInterval = result.appendText(bulletSymbol)

        result.setSpan(object : ColorTextClickableSpan(bulletSymbol, timeColor) {

            override fun onClick(widget: View) {
                callOnClick()
            }

        }, bulletInterval)

        if(postHeader.communityId!!.code == GlobalConstants.MY_FEED_COMMUNITY_ID) {
            postHeader.communityName?.let {
                val userNameTextColor = ContextCompat.getColor(context, R.color.post_header_user_name_text)
                val userNameInterval = result.appendText(it)
                result.setSpan(object : ColorTextClickableSpan(it, userNameTextColor) {
                    override fun onClick(widget: View) {
                        onCommunityClickListener?.invoke(postHeader.communityId)
                    }

                }, userNameInterval)
            }
        } else {
            postHeader.userName?.let {
                val userNameTextColor = ContextCompat.getColor(context, R.color.post_header_user_name_text)
                val userNameInterval = result.appendText(it)
                result.setSpan(object : ColorTextClickableSpan(it, userNameTextColor) {
                    override fun onClick(widget: View) {
                        onUserClickListener?.invoke(userId)
                    }

                }, userNameInterval)
            }
        }
        return result
    }
}
