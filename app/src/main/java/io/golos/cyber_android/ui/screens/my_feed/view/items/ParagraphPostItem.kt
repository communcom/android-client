package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.ParagraphWidget
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock

class ParagraphPostItem(paragraphBlock: ParagraphBlock) :
    BasePostBlockItem<ParagraphBlock, ParagraphWidget>(paragraphBlock) {

    override fun createWidgetView(context: Context): ParagraphWidget = ParagraphWidget(context)
}