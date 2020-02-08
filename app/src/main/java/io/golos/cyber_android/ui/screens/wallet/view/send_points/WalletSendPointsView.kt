package io.golos.cyber_android.ui.screens.wallet.view.send_points

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_send_points.view.*

class WalletSendPointsView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var adapter: WalletSendPointsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    init {
        inflate(context, R.layout.view_wallet_send_points, this)
    }

    fun setItems(pageSize: Int, items: List<VersionedListItem>, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        if(!::adapter.isInitialized) {
            layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            adapter = WalletSendPointsAdapter(listItemEventsProcessor, pageSize)
            adapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = layoutManager
            itemsList.adapter = adapter
        }

        adapter.update(items)
    }
}