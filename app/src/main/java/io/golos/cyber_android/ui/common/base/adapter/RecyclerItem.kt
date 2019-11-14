package io.golos.cyber_android.ui.common.base.adapter

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes

interface RecyclerItem {

    @LayoutRes
    fun getLayoutId(): Int

    fun renderView(context: Context, view: View)

    fun initView(context: Context, view: View)

    fun onViewRecycled(view: View) {}

    fun areItemsTheSame(): Int = hashCode()

}