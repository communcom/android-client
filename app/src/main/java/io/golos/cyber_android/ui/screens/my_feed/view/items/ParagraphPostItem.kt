package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.ParagraphWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.ParagraphWidgetListener
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock

class ParagraphPostItem(val paragraphBlock: ParagraphBlock, widgetListener: ParagraphWidgetListener?) :
    BasePostBlockItem<ParagraphBlock, ParagraphWidgetListener, ParagraphWidget>(paragraphBlock, widgetListener) {

    override fun createWidgetView(context: Context): ParagraphWidget = ParagraphWidget(context)

    override fun areItemsTheSame(): Int = paragraphBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if(item is ParagraphPostItem){
            return paragraphBlock == item.paragraphBlock
        }
        return false
    }
}