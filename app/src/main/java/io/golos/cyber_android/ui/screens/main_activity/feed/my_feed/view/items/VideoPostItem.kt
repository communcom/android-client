package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedVideoWidget
import io.golos.domain.use_cases.post.post_dto.VideoBlock

class VideoPostItem(videoBlock: VideoBlock) :
    BasePostBlockItem<VideoBlock, EmbedVideoWidget>(videoBlock) {

    override fun createWidgetView(context: Context): EmbedVideoWidget = EmbedVideoWidget(context)
}