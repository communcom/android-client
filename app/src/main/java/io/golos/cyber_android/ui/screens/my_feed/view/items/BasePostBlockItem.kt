package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.BasePostBlockWidgetListener
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.PostBlockWidget
import io.golos.domain.use_cases.post.post_dto.Block
import kotlinx.android.synthetic.main.item_post_block.view.*

abstract class BasePostBlockItem<POST_BLOCK : Block, WIDGET_LISTENER: BasePostBlockWidgetListener, WIDGET : PostBlockWidget<POST_BLOCK, WIDGET_LISTENER>>(private val postBlock: POST_BLOCK,
                                                                                           private val widgetListener: WIDGET_LISTENER? = null) :
    BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_post_block

    private var widgetViewId: Int = -1

    protected abstract fun createWidgetView(context: Context): WIDGET

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        widgetViewId = View.generateViewId()
        val widgetView = createWidgetView(context)
        widgetView.setOnClickProcessor(widgetListener)
        (widgetView as View).id = widgetViewId
        view.postWidgetContainer.addView(widgetView)
    }

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        (view.postWidgetContainer.findViewById<View>(widgetViewId) as WIDGET).render(postBlock)
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)
        (view.postWidgetContainer.findViewById<View>(widgetViewId) as WIDGET).release()
    }
}