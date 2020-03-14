package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.net.Uri
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.domain.dto.RewardPostDomain

interface BasePostBlockWidgetListener

interface RichWidgetListener : BasePostBlockWidgetListener {

    fun onLinkClicked(linkUri: Uri)

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(contentId: ContentId)
}

interface EmbedWidgetListener : BasePostBlockWidgetListener {

    fun onLinkClicked(linkUri: Uri)

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(contentId: ContentId)
}

interface AttachmentWidgetListener : BasePostBlockWidgetListener

interface EmbedImageWidgetListener : BasePostBlockWidgetListener {

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(contentId: ContentId)
}

interface EmbedVideoWidgetListener : BasePostBlockWidgetListener {
    fun onItemClicked(contentId: ContentId)
}

interface EmbedWebsiteWidgetListener : BasePostBlockWidgetListener,
    LinkListener

interface ParagraphWidgetListener : BasePostBlockWidgetListener,
    LinkListener,
    SeeMoreListener,
    BlockBodyListener{

    fun onUserClicked(userId: String)

    fun onCommunityClicked(communityCode: String)
}

interface MenuListener {
    fun onMenuClicked(postMenu: PostMenu)
}

interface LinkListener {

    fun onLinkClicked(linkUri: Uri)
}

interface SeeMoreListener {
    fun onSeeMoreClicked(contentId: ContentId)
}

interface BlockBodyListener {
    fun onBodyClicked(postContentId: ContentId?)
}

interface RewardListener {
    fun onRewardClick(reward: RewardPostDomain?)
}
