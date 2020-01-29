package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.spans.MovementMethod
import io.golos.cyber_android.ui.shared.utils.toTimeEstimateFormat
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
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
    private var onCommunityClickListener: ((String) -> Unit)? = null //CommunityId as param

    private lateinit var userId: String

    init {
        inflate(getContext(), R.layout.view_post_viewer_header, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        joinToCommunityButton.setOnClickListener { onJoinToCommunityButtonClickListener?.invoke() }
        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }

    }

    fun setHeader(postHeader: PostHeader) {
        userId = postHeader.userId
        communityTitle.movementMethod = object: MovementMethod(){

            override fun onEmptyClicked(): Boolean {
                callOnClick()
                return super.onEmptyClicked()
            }
        }
        val builder = SpannableStringBuilder()
        postHeader.communityName?.let {
            val textInterval = builder.appendText(it)
            val communityNameTextColor = ContextCompat.getColor(context, R.color.post_header_community_text)
            builder.setSpan(object : ColorTextClickableSpan(it, communityNameTextColor) {

                override fun onClick(widget: View) {
                    postHeader.communityId?.let { id ->
                        onCommunityClickListener?.invoke(id)
                    }
                }

            }, textInterval)
        }


        communityTitle.text = builder

        authorAndTime.movementMethod = object: MovementMethod(){

            override fun onEmptyClicked(): Boolean {
                callOnClick()
                return super.onEmptyClicked()
            }
        }

        authorAndTime.text = getTimeAndAuthor(postHeader)

        postHeader.communityAvatarUrl
            ?.let {
                Glide.with(communityAvatar)
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .into(communityAvatar)
            }

        communityAvatar.setOnClickListener {
            postHeader.communityId?.let { id ->
                onCommunityClickListener?.invoke(id)
            }
        }

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

    fun setOnCommunityClickListener(listener: ((String) -> Unit)?) {
        onCommunityClickListener = listener
    }

    fun release(){
        setOnUserClickListener(null)
        setOnMenuButtonClickListener(null)
        setOnJoinToCommunityButtonClickListener(null)
        setOnBackButtonClickListener(null)
        communityAvatar.clear()
    }

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

        postHeader.userName?.let {
            val userNameTextColor = ContextCompat.getColor(context, R.color.post_header_user_name_text)
            val userNameInterval = result.appendText(it)
            result.setSpan(object : ColorTextClickableSpan(it, userNameTextColor) {

                override fun onClick(widget: View) {
                    onUserClickListener?.invoke(userId)
                }

            }, userNameInterval)
        }
        return result
    }
}
