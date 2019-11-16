package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedImageWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedImageWidgetListener
import io.golos.domain.use_cases.post.post_dto.ImageBlock

class ImagePostItem(imageBlock: ImageBlock, widgetListener: EmbedImageWidgetListener?) :
    BasePostBlockItem<ImageBlock, EmbedImageWidgetListener, EmbedImageWidget>(imageBlock, widgetListener) {

    override fun createWidgetView(context: Context): EmbedImageWidget = EmbedImageWidget(context)
}