package io.golos.cyber_android.ui.common.widgets.post

import android.net.Uri
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu

interface BasePostBlockWidgetListener

interface RichWidgetListener : BasePostBlockWidgetListener {

    fun onLinkClicked(linkUri: Uri)

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(postContentId: Post.ContentId)
}

interface EmbedWidgetListener : BasePostBlockWidgetListener {

    fun onLinkClicked(linkUri: Uri)

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(postContentId: Post.ContentId)
}

interface AttachmentWidgetListener : BasePostBlockWidgetListener

interface EmbedImageWidgetListener : BasePostBlockWidgetListener {

    fun onImageClicked(imageUri: Uri)

    fun onItemClicked(postContentId: Post.ContentId)
}

interface EmbedVideoWidgetListener : BasePostBlockWidgetListener {
    fun onItemClicked(postContentId: Post.ContentId)
}

interface EmbedWebsiteWidgetListener : BasePostBlockWidgetListener,
    LinkListener

interface ParagraphWidgetListener : BasePostBlockWidgetListener,
    LinkListener, SeeMoreListener {

    fun onUserClicked(userId: String)
}

interface MenuListener {
    fun onMenuClicked(postMenu: PostMenu)
}

interface LinkListener {

    fun onLinkClicked(linkUri: Uri)
}

interface SeeMoreListener {
    fun onSeeMoreClicked(postContentId: Post.ContentId)
}
