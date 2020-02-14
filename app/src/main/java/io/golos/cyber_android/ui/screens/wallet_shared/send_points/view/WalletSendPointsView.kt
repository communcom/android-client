package io.golos.cyber_android.ui.screens.wallet_shared.send_points.view

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

    private var pageSize: Int = 0
    private lateinit var listItemEventsProcessor: WalletSendPointsListItemEventsProcessor

    private var onSeeAllClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_send_points, this)

        seeAllLabel.setOnClickListener { onSeeAllClickListener?.invoke() }
    }

    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
    }

    fun setEventsProcessor(listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        this.listItemEventsProcessor = listItemEventsProcessor
    }

    fun setItems(items: List<VersionedListItem>) {
        if(!::adapter.isInitialized) {
            layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            adapter = WalletSendPointsAdapter(
                listItemEventsProcessor,
                pageSize
            )
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