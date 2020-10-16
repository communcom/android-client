package io.golos.cyber_android.ui.screens.wallet.view.my_points

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import kotlinx.android.synthetic.main.view_wallet_my_points.view.*

class WalletMyPointsView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var adapter: WalletMyPointsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var listItemEventsProcessor: WalletMyPointsListItemEventsProcessor

    private var onSeeAllClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_my_points, this)
        seeAllLabel.setOnClickListener { onSeeAllClickListener?.invoke() }
    }

    fun setEventsProcessor(listItemEventsProcessor: WalletMyPointsListItemEventsProcessor) {
        this.listItemEventsProcessor = listItemEventsProcessor
    }

    fun setItems(items: List<MyPointsListItem>) {
        if(!::adapter.isInitialized) {
            layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            adapter = WalletMyPointsAdapter(listItemEventsProcessor)
            adapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = layoutManager
            itemsList.adapter = adapter
        }

        adapter.update(items)
    }

    fun setOnSeeAllClickListener(listener: (() -> Unit)?) {
        onSeeAllClickListener = listener
    }
}