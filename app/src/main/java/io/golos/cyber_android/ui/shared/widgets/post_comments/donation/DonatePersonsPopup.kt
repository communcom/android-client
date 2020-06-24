package io.golos.cyber_android.ui.shared.widgets.post_comments.donation

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DimenRes
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.DonatorDomain

class DonatePersonsPopup {
    fun show(anchorView: View, donations: DonationsDomain) {
        val panelSize = when(donations.donators.size) {
            1 -> 0.275f
            2 -> 0.35f
            3 -> 0.425f
            else -> 0.65f
        }

        val balloon = Balloon.Builder(anchorView.context)
            .setArrowVisible(true)
            .setArrowPosition(0.525f)
            .setArrowSize(getDimenValue(R.dimen.donates_popup_arrow_height, anchorView))
            .setArrowColorResource(R.color.blue_button)
            .setLifecycleOwner(anchorView.parentActivity)
            .setWidthRatio(panelSize)
            .setHeight(getDimenValue(R.dimen.donates_popup_height_full, anchorView))
            .setPadding(0)
            .setAutoDismissDuration(5_000L)
            .setDismissWhenTouchOutside(true)
            .setCornerRadius(getDimenValue(R.dimen.donates_popup_corners, anchorView).toFloat())
            .setLayout(R.layout.view_donation_popup_persons)
            .build()

        val contentView = balloon.getContentView()
        contentView.setBackgroundResource(R.drawable.bcg_donate_popup)

        contentView.findViewById<View>(R.id.buttonClose).setOnClickListener { balloon.dismiss() }
        initDonationInfo(contentView, donations.donators)

        balloon.showAlignTop(anchorView)
    }

    private fun getDimenValue(@DimenRes dimenResId: Int, view: View): Int =
        (view.resources.getDimension(dimenResId) / view.resources.displayMetrics.density).toInt()

    @SuppressLint("SetTextI18n")
    private fun initDonationInfo(contentView: View, donators: List<DonatorDomain>) {
        val firstUserAvatar = contentView.findViewById<ImageView>(R.id.firstUser)
        val secondUserAvatar = contentView.findViewById<ImageView>(R.id.secondUser)
        val thirdUserAvatar = contentView.findViewById<ImageView>(R.id.thirdUser)
        val donationLabel = contentView.findViewById<TextView>(R.id.donationLabel)

        if(donators.size > 3) {
            donationLabel.text = "+${donators.size - 3} ${contentView.context.getString(R.string.donate_title)}"
            donationLabel.visibility = View.VISIBLE
        } else {
            donationLabel.visibility = View.INVISIBLE
        }

        firstUserAvatar.loadAvatar(donators[0].person.avatarUrl)

        if(donators.size < 2) {
            secondUserAvatar.visibility = View.INVISIBLE
        } else {
            secondUserAvatar.visibility = View.VISIBLE
            secondUserAvatar.loadAvatar(donators[1].person.avatarUrl)
        }

        if(donators.size < 3) {
            thirdUserAvatar.visibility = View.INVISIBLE
        } else {
            thirdUserAvatar.visibility = View.VISIBLE
            thirdUserAvatar.loadAvatar(donators[2].person.avatarUrl)
        }
    }
}