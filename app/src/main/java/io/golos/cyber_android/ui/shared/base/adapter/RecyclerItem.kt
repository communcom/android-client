package io.golos.cyber_android.ui.shared.base.adapter

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes

interface RecyclerItem {

    var adapterPosition: Int

    @LayoutRes
    fun getLayoutId(): Int

    fun renderView(context: Context, view: View)

    fun initView(context: Context, view: View)

    fun onViewRecycled(view: View) {}

    //Need return indificator for item, default is hashcode
    fun areItemsTheSame(): Int = hashCode()

    //Define that content same: default use equals
    fun areContentsSame(item: RecyclerItem): Boolean = this == item
}