package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselAdapter
import io.golos.cyber_android.ui.shared.animation.AnimationUtils
import io.golos.domain.dto.CommunityIdDomain
import io.golos.utils.format.CurrencyFormatter
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.view_wallet_send_points_expanded_top_panel.view.*
import kotlinx.android.synthetic.main.view_wallet_send_points_expanded_top_panel.view.backButton
import kotlinx.android.synthetic.main.view_wallet_send_points_expanded_top_panel.view.selectCommunityButton

class WalletSendPointsExpandedTopPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onItemSelectedListener: ((CommunityIdDomain) -> Unit)? = null
    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onSelectCommunityButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_send_points_expanded_top_panel, this)

        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        selectCommunityButton.setOnClickListener { onSelectCommunityButtonClickListener?.invoke() }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if(oldh == 0) {
            return
        }

        AnimationUtils.getFloatAnimator(
            duration = 400,
            forward = h > oldh,
            updateListener = { alpha ->
                children.forEach {
                    it.alpha = alpha
                }
            }
        ).start()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun setData(data: WalletCommunityBalanceRecordDomain) {
        name.text = data.communityName ?: data.communityId.code
        amount.text = CurrencyFormatter.format(data.points)
    }

    fun setCarouselStartData(data: CarouselStartData) {
        val adapter = CarouselAdapter(R.layout.view_wallet_carousel)
        carousel.addAdapter(adapter)
        adapter.setItems(data.items)
        carousel.setUp(data.startIndex) { onItemSelectedListener?.invoke(CommunityIdDomain(it)) }
    }

    fun setTitle(@StringRes titleResId: Int) = title.setText(titleResId)

    fun switchMode(isInCarouselMode: Boolean) {
        carousel.visibility = if(isInCarouselMode) View.VISIBLE else View.INVISIBLE
        communSymbol.visibility = if(isInCarouselMode) View.INVISIBLE else View.VISIBLE
    }

    fun setMenuVisibility(isVisible: Boolean) {
        selectCommunityButton.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
    }

    fun setCarouselPosition(position: Int) = carousel.scrollToPosition(position)

    fun setOnItemSelectedListener(listener: ((CommunityIdDomain) -> Unit)?) {
        onItemSelectedListener = listener
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnSelectCommunityButtonClickListener(listener: (() -> Unit)?){
        onSelectCommunityButtonClickListener = listener
    }
}