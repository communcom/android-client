package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselAdapter
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.view_wallet_send_points_expanded_top_panel.view.*
import kotlinx.android.synthetic.main.view_wallet_send_points_expanded_top_panel.view.carousel

class WalletSendPointsExpandedTopPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onItemSelectedListener: ((String) -> Unit)? = null
    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onSelectCommunityButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_send_points_expanded_top_panel, this)

        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        selectCommunityButton.setOnClickListener { onSelectCommunityButtonClickListener?.invoke() }
    }

    fun setData(data: WalletCommunityBalanceRecordDomain) {
        name.text = data.communityName ?: data.communityId
        amount.text = CurrencyFormatter.format(data.points)
    }

    fun setCarouselStartData(data: CarouselStartData) {
        val adapter = CarouselAdapter(R.layout.view_wallet_carousel_large)
        carousel.addAdapter(adapter)
        adapter.setItems(data.items)
        carousel.setUp(data.startIndex) { onItemSelectedListener?.invoke(it) }
    }

    fun setCarouselPosition(position: Int) = carousel.scrollToPosition(position)

    fun setOnItemSelectedListener(listener: ((String) -> Unit)?) {
        onItemSelectedListener = listener
    }


    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnSelectCommunityButtonClickListener(listener: (() -> Unit)?){
        onSelectCommunityButtonClickListener = listener
    }
}