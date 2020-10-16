package io.golos.cyber_android.ui.dialogs.donation

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.RewardInfo
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.cyber_android.ui.shared.utils.toTimeEstimateFormat
import io.golos.domain.dto.*
import io.golos.use_cases.reward.getRewardValue
import io.golos.use_cases.reward.isRewarded
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan
import kotlinx.android.synthetic.main.dialog_donations_users.*

class DonationUsersDialog(private val post: Post) : BottomSheetDialogFragmentBase<DonationUsersDialog.Result>() {
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onDonateClickListener: ((DonateType, ContentIdDomain, CommunityIdDomain, UserBriefDomain) -> Unit)? = null

    sealed class Result {
        data class ItemSelected(val user: UserIdDomain) : Result()
    }

    companion object {

        fun show(parent: Fragment, post: Post, closeAction: (Result?) -> Unit, onUserClickListener: ((String) -> Unit),onDonateClickListener: ((DonateType, ContentIdDomain, CommunityIdDomain, UserBriefDomain) -> Unit)) =
            DonationUsersDialog(post).apply {
                closeActionListener = closeAction
                this.onUserClickListener = onUserClickListener
                this.onDonateClickListener = onDonateClickListener
            }.show(parent.parentFragmentManager, "DONATION_USERS_DIALOG")
    }

    override val closeButton: View?
        get() = ivClose

    override val layout: Int
        get() = R.layout.dialog_donations_users


    override fun setupView() {
        val onClickAction: (UserIdDomain) -> Unit = { closeOnItemSelected(Result.ItemSelected(it)) }
        post.donation?.let {
            var donationCount = 0.0
            for (i in it.donators.indices) {
                val userView = DonationUsersDialogItem(requireContext())
                userView.init(it.contentId, it.donators[i], i == it.donators.lastIndex, onClickAction)
                itemsContainer.addView(userView)
                donationCount = donationCount.plus(it.donators[i].amount.toDouble())
            }
            vDonatesPoint.text = donationCount.toString()
            lNoDonateContainer.visibility=View.GONE
            vDonateList.visibility=View.VISIBLE
        } ?: run {
            vDonatesPoint.text = "0"
            lNoDonateContainer.visibility=View.VISIBLE
            vDonateList.visibility=View.GONE
        }
        communityAvatar.loadAvatar(post.community.avatarUrl)

        context?.run {
            authorAndTime.adjustSpannableClicks()
            authorAndTime.text = getTimeAndAuthor(post, this)
        }

        vRewardPoint.text = post.reward?.rewardValue?.value.toString()
        vName.text = post.community.name
        vDonate.setOnClickListener {
            onDonateClickListener?.invoke(DonateType.DONATE_OTHER, post.contentId, post.community.communityId, post.author)
            dismiss()
        }
        vDonateInNoYetDonation.setOnClickListener {
            onDonateClickListener?.invoke(DonateType.DONATE_OTHER, post.contentId, post.community.communityId, post.author)
            dismiss()
        }


    }

    private fun getTimeAndAuthor(post: Post, context: Context): SpannableStringBuilder {

        val community = post.community
        val author = post.author
        val userId = author.userId.userId
        val postHeader =
            PostHeader(community.name, community.avatarUrl, community.communityId, post.meta.creationTime, author.username, author.userId.userId, author.avatarUrl, canJoinToCommunity = false, isBackFeatureEnabled = false,
                reward = takeIf { post.reward.isRewarded() }?.let {
                    RewardInfo(rewardValueInPoints = post.reward.getRewardValue(), rewardValueInCommun = post.reward?.rewardValueCommun, rewardValueInUSD = post.reward?.rewardValueUSD, rewardCurrency = RewardCurrency.POINTS)
                })
        val result = SpannableStringBuilder()

        val time = postHeader.actionDateTime.toTimeEstimateFormat(context)
        val timeInterval = result.appendText(time)

        val timeColor = ContextCompat.getColor(context, R.color.post_header_time_text)

        result.setSpan(object : ColorTextClickableSpan(time, timeColor) {

            override fun onClick(widget: View) {

            }

        }, timeInterval)

        val bulletSymbol = " ${SpecialChars.BULLET} "
        val bulletInterval = result.appendText(bulletSymbol)

        result.setSpan(object : ColorTextClickableSpan(bulletSymbol, timeColor) {

            override fun onClick(widget: View) {

            }

        }, bulletInterval)


        postHeader.userName?.let {
            val userNameTextColor = ContextCompat.getColor(context, R.color.post_header_user_name_text)
            val userNameInterval = result.appendText(it)
            result.setSpan(object : ColorTextClickableSpan(it, userNameTextColor) {
                override fun onClick(widget: View) {
                    onUserClickListener?.invoke(userId)
                    dismiss()
                }

            }, userNameInterval)
        }

        return result
    }

}