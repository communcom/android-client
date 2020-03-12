package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.animation.AnimationUtils
import io.golos.utils.format.CurrencyFormatter
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if(oldh == 0) {
            return
        }

        AnimationUtils.getFloatAnimator(
            duration = 400,
            forward = h < oldh,
            updateListener = { alpha ->
                children.forEach {
                    it.alpha = alpha
                }
            }
        ).start()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    @SuppressLint("SetTextI18n")
    fun setData(data: WalletCommunityBalanceRecordDomain) {
        loadTarget?.clear(context)
        loadTarget = pointsLogo.load(data.communityLogoUrl, R.drawable.ic_commun)

        pointsName.text = "${data.communityName ?: data.communityId} ${CurrencyFormatter.format(data.points)}"
    }

    fun setMenuVisibility(isVisible: Boolean) {
        selectCommunityButton.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnSelectCommunityButtonClickListener(listener: (() -> Unit)?){
        onSelectCommunityButtonClickListener = listener
    }
}
