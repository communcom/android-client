package io.golos.cyber_android.ui.shared.base.adapter

import android.content.Context
import android.view.View
import timber.log.Timber

abstract class BaseRecyclerItem : RecyclerItem {

    private var initView = false

    override fun initView(context: Context, view: View) {
        initView = true
    }

    override fun renderView(context: Context, view: View) {
        if (!initView) {
            Timber.d("renderView")
            initView(context, view)
        }
    }

    override var adapterPosition: Int = -1
}