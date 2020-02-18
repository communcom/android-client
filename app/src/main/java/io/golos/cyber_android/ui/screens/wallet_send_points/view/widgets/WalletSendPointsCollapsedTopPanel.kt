package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.glide.GlideTarget
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.load
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.view_wallet_send_points_collapsed_top_panel.view.*

class WalletSendPointsCollapsedTopPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var loadTarget: GlideTarget? = null

    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onSelectCommunityButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_send_points_collapsed_top_panel, this)

        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        selectCommunityButton.setOnClickListener { onSelectCommunityButtonClickListener?.invoke() }
    }

    @SuppressLint("SetTextI18n")
    fun setData(data: WalletCommunityBalanceRecordDomain) {
        loadTarget?.clear(context)
        loadTarget = pointsLogo.load(data.communityLogoUrl, R.drawable.ic_commun)

        pointsName.text = "${data.communityName ?: data.communityId} ${CurrencyFormatter.format(data.points)}"
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnSelectCommunityButtonClickListener(listener: (() -> Unit)?){
        onSelectCommunityButtonClickListener = listener
    }
}
