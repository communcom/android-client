package io.golos.cyber_android.ui.dialogs.donation

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.DonatorDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.view_donations_users_item.view.*

class DonationUsersDialogItem
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onItemClickListener: ((UserIdDomain) -> Unit)? = null
    private lateinit var userId: UserIdDomain

    init {
        inflate(getContext(), R.layout.view_donations_users_item, this)

        setOnClickListener { onItemClickListener?.invoke(userId) }
    }

    @SuppressLint("SetTextI18n")
    fun init(
        postId: ContentIdDomain,
        donationData: DonatorDomain,
        isLastItem: Boolean,
        onItemClickListener: ((UserIdDomain) -> Unit)?) {

        this.onItemClickListener = onItemClickListener
        userId = donationData.person.userId

        avatar.loadAvatar(donationData.person.avatarUrl)
        name.text = donationData.person.username ?: userId.userId

        amount.text = "+ ${CurrencyFormatter.formatShort(context, donationData.amount.toDouble())} ${context.getString(R.string.points)}"

        itemsGap.visibility = if(isLastItem) View.GONE else View.VISIBLE
    }
}
