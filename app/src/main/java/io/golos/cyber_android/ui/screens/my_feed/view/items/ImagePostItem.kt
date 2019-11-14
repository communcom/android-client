package io.golos.cyber_android.ui.screens.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.AttachmentsWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedImageWidget
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.ImageBlock

class ImagePostItem(imageBlock: ImageBlock) :
    BasePostBlockItem<ImageBlock, EmbedImageWidget>(imageBlock) {

    override fun createWidgetView(context: Context): EmbedImageWidget = EmbedImageWidget(context)
}