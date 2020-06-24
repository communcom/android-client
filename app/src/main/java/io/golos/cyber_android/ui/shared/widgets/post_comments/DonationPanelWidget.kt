package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.view_donation_post.view.*

class DonationPanelWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.view_donation_post, this)
    }

    fun setAmount(amount: Double) {
        amountLabel.text = CurrencyFormatter.formatShort(context, amount)
        pointsLabel.text = context.resources.getQuantityString(R.plurals.plural_points_formatted_post, amount.toInt())
    }
}
