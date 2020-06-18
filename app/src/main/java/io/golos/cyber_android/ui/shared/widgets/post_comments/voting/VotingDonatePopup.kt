package io.golos.cyber_android.ui.shared.widgets.post_comments.voting

import android.view.View
import androidx.annotation.DimenRes
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.shared.extensions.parentActivity

class VotingDonatePopup {
    fun show(anchorView: View, actions: (DonateType) -> Unit) {
        val balloon = Balloon.Builder(anchorView.context)
            .setArrowVisible(true)
            .setArrowPosition(0.15f)
            .setArrowSize(getDimenValue(R.dimen.donates_popup_arrow_height, anchorView))
            .setArrowColorResource(R.color.blue_button)
            .setLifecycleOwner(anchorView.parentActivity)
            .setWidthRatio(0.98f)
            .setHeight(getDimenValue(R.dimen.donates_popup_height_full, anchorView))
            .setPadding(0)
            .setAutoDismissDuration(5_000L)
            .setDismissWhenTouchOutside(true)
            .setCornerRadius(getDimenValue(R.dimen.donates_popup_corners, anchorView).toFloat())
            .setLayout(R.layout.view_donat_popup)
            .build()

        val contentView = balloon.getContentView()
        contentView.setBackgroundResource(R.drawable.bcg_donate_popup)

        contentView.findViewById<View>(R.id.buttonClose).setOnClickListener { balloon.dismiss() }

        contentView.findViewById<View>(R.id.buttonPoints10).setOnClickListener {
            balloon.dismiss()
            actions(DonateType.DONATE_10)
        }

        contentView.findViewById<View>(R.id.buttonPoints100).setOnClickListener {
            balloon.dismiss()
            actions(DonateType.DONATE_100)
        }

        contentView.findViewById<View>(R.id.buttonPoints1000).setOnClickListener {
            balloon.dismiss()
            actions(DonateType.DONATE_1000)
        }

        contentView.findViewById<View>(R.id.buttonPointsOther).setOnClickListener {
            balloon.dismiss()
            actions(DonateType.DONATE_OTHER)
        }

        balloon.showAlignTop(anchorView)
    }

    private fun getDimenValue(@DimenRes dimenResId: Int, view: View): Int =
        (view.resources.getDimension(dimenResId) / view.resources.displayMetrics.density).toInt()
}