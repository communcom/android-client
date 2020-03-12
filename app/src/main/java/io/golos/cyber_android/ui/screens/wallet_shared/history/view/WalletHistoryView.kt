package io.golos.cyber_android.ui.screens.wallet_shared.history.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_history.view.*

class WalletHistoryView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var historyAdapter: WalletHistoryAdapter
    private lateinit var historyLayoutManager: LinearLayoutManager

    private var pageSize: Int = 0
    private lateinit var listItemEventsProcessor: WalletHistoryListItemEventsProcessor

    init {
        inflate(context, R.layout.view_wallet_history, this)
    }

    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
    }

    fun setEventsProcessor(listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        this.listItemEventsProcessor = listItemEventsProcessor
    }

    fun setItems(items: List<VersionedListItem>) {
        if(!::historyAdapter.isInitialized) {
            historyLayoutManager = LinearLayoutManager(context)

            historyAdapter = WalletHistoryAdapter(
                listItemEventsProcessor,
                pageSize
            )
            historyAdapter.setHasStableIds(true)

            historyList.isSaveEnabled = false
            historyList.itemAnimator = null
            historyList.layoutManager = historyLayoutManager
            historyList.adapter = historyAdapter
        }

        historyAdapter.update(items)
    }

}