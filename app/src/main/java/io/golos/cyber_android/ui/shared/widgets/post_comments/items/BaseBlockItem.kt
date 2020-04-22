package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.widgets.post_comments.BasePostBlockWidgetListener
import io.golos.cyber_android.ui.shared.widgets.post_comments.BlockWidget
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.Block
import kotlinx.android.synthetic.main.item_post_block.view.*

abstract class BaseBlockItem<POST_BLOCK : Block, WIDGET_LISTENER : BasePostBlockWidgetListener, WIDGET : BlockWidget<POST_BLOCK, WIDGET_LISTENER>>(
    private val postBlock: POST_BLOCK,
    private val widgetListener: WIDGET_LISTENER? = null,
    private val onLongClickLister: View.OnLongClickListener? = null
) : BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_post_block

    private var widgetViewId: Int = -1

    protected abstract fun createWidget(context: Context): WIDGET

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        widgetViewId = View.generateViewId()
        val widget = createWidget(context)
        widget.setOnClickProcessor(widgetListener)
        val widgetView = widget as View
        widgetView.id = widgetViewId
        //Handle long click all widgets except [io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidget]
        widget.setOnLongClickListener(onLongClickLister)
        //only work on [io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidget]
        val postWidgetContainer = view.postWidgetContainer
        postWidgetContainer.setOnLongClickListener(onLongClickLister)
        postWidgetContainer.addView(widgetView)

        if(adapterPosition == 0){
            postWidgetContainer.setPadding(
                postWidgetContainer.paddingLeft,
                0,
                postWidgetContainer.paddingRight,
                postWidgetContainer.paddingBottom
            )
        } else{
            postWidgetContainer.setPadding(
                postWidgetContainer.paddingLeft,
                postWidgetContainer.context.resources.getDimension(R.dimen.post_widget_padding_top).toInt(),
                postWidgetContainer.paddingRight,
                postWidgetContainer.paddingBottom
            )
        }
    }

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        (view.postWidgetContainer.findViewById<View>(widgetViewId) as WIDGET).render(postBlock)
    }

    override fun onViewRecycled(view: View) {
        (view.postWidgetContainer.findViewById<View>(widgetViewId) as WIDGET).release()
        view.postWidgetContainer.removeAllViews()
        super.onViewRecycled(view)
    }
}