package io.golos.cyber_android.ui.shared.widgets.rules_card

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_card_rules.view.*

class RulesCard
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_card_rules, this)
    }

    fun setData(item: RulesCardItem) {
        when {
            !item.title.isNullOrEmpty() && !item.text.isNullOrEmpty() -> {
                rulesTitle.text = item.title
                rulesText.text = item.text
            }

            (!item.title.isNullOrEmpty() && item.text.isNullOrEmpty()) ||
            (item.title.isNullOrEmpty() && !item.text.isNullOrEmpty()) -> {
                rulesTitle.visibility = View.GONE
                rulesText.text = item.title
            }
        }
    }
}