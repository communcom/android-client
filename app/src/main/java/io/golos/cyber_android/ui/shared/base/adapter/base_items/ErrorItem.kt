package io.golos.cyber_android.ui.shared.base.adapter.base_items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import kotlinx.android.synthetic.main.item_progress_error.view.*

class ErrorItem(
    private val listener: View.OnClickListener
): BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_progress_error

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        view.pbPageLoading.visibility = View.INVISIBLE
        view.btnPageLoadingRetry.visibility = View.VISIBLE
        view.btnPageLoadingRetry.setOnClickListener(listener)
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)
        view.btnPageLoadingRetry.setOnClickListener(null)
    }
}