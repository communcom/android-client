package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared.widgets.post_comments.donation.DonatePersonsPopup
import io.golos.domain.dto.*
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.view_donation_post.view.*

class DonationPanelWidget
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var donateUsersListListener: ((DonateType, ContentIdDomain, CommunityIdDomain, UserBriefDomain) -> Unit)

    init {
        inflate(getContext(), R.layout.view_donation_post, this)
    }

    fun setAmount(post: Post) {
        post.donation?.let { domain ->
            if (domain.donators.size == 1) {
                vDonateUserInfo.text = domain.donators[0].person.username
            } else {
                vDonateUserInfo.text =
                    String.format("%s %s", domain.donators[0].person.username, resources.getString(R.string.and_other))
            }
            vDonateStateIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_commun_with_donate))
        } ?: kotlin.run {
            vDonateUserInfo.text = resources.getText(R.string.default_first_donate)
            vDonateStateIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_smile))
        }
        vDonate.setOnClickListener {
            donateUsersListListener.invoke(DonateType.DONATE_OTHER, post.contentId, post.community.communityId, post.author)
        }
    }

    fun setDonateUserListListener(listener: ((DonateType, ContentIdDomain, CommunityIdDomain, UserBriefDomain) -> Unit)) {
        donateUsersListListener = listener
    }
}