package io.golos.cyber_android.ui.screens.wallet_point.view.prime_panel

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselAdapter
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import kotlinx.android.synthetic.main.view_wallet_point_prime_panel.view.*

class WalletPointPrimePanelView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onItemSelectedListener: ((String) -> Unit)? = null
    private var onBackButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_point_prime_panel, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
    }

    fun setTitle(title: String) {
        titleValue.text = title
    }

    fun setValue(value: Double) {
        textValue.text = CurrencyFormatter.format(value)
    }

    fun setValueCommun(value: Double) {
        textValueInCommuns.text = context.resources.getFormattedString(R.string.commun_format, CurrencyFormatter.format(value))
    }

    fun setAvailableHoldBarValue(value: Double) {
        availableHoldView.setInnerSizeFactor(value.toFloat())
    }

    fun setAvailableValue(value: Double) {
        availableValue.text = CurrencyFormatter.format(value)
    }

    @SuppressLint("SetTextI18n")
    fun setHoldValue(value: Double) {
        holdValue.text = " / ${CurrencyFormatter.format(value)}"
    }

    fun setCarouselStartData(data: CarouselStartData) {
        val adapter = CarouselAdapter()
        carousel.addAdapter(adapter)
        adapter.setItems(data.items)
        carousel.setUp(data.startIndex) { onItemSelectedListener?.invoke(it) }
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnItemSelectedListener(listener: ((String) -> Unit)?) {
        onItemSelectedListener = listener
    }

    fun setOnSendClickListener(listener: (() -> Unit)?) = bottomArea.setOnSendClickListener(listener)

    fun setOnBuyClickListener(listener: (() -> Unit)?) = bottomArea.setOnBuyClickListener(listener)

    fun setOnConvertClickListener(listener: (() -> Unit)?) = bottomArea.setOnConvertClickListener(listener)
}