package io.golos.cyber_android.ui.shared.widgets.rules_card

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.getStyledAttribute

class RulesCardList
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var cardMarginStart = 0
    private var cardMarginEnd = 0
    private var cardMarginTop = 0
    private var cardMarginBottom = 0
    private var cardCornerRadius = 0f

    init {
        orientation =  VERTICAL
        attrs?.let { retrieveAttributes(attrs) }
    }

    fun setData(items: List<RulesCardItem>) {
        if(items.isEmpty()) {
            addCard(RulesCardItem(null, context.getString(R.string.missing_description)))
        } else {
            items.forEach { addCard(it) }
        }
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulesCardList)

        cardMarginStart = typedArray.getDimension(R.styleable.RulesCardList_card_margin_start, 10f).toInt()
        cardMarginEnd = typedArray.getDimension(R.styleable.RulesCardList_card_margin_end, 10f).toInt()
        cardMarginTop = typedArray.getDimension(R.styleable.RulesCardList_card_margin_top, 10f).toInt()
        cardMarginBottom = typedArray.getDimension(R.styleable.RulesCardList_card_margin_bottom, 10f).toInt()

        cardCornerRadius = typedArray.getDimension(R.styleable.RulesCardList_card_corner_radius, 10f)

        typedArray.recycle()
    }

    private fun addCard(item: RulesCardItem) {
        val card = RulesCard(context).apply { setData(item) }
        addView(card)

        val layoutParams = card.layoutParams as LinearLayout.LayoutParams
        layoutParams.marginStart = cardMarginStart
        layoutParams.marginEnd = cardMarginEnd
        layoutParams.topMargin = cardMarginTop
        layoutParams.bottomMargin = cardMarginBottom

        card.layoutParams = layoutParams

        card.radius = cardCornerRadius
        card.setCardBackgroundColor(getStyledAttribute(R.attr.white, context))
    }
}