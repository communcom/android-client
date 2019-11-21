package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets

import android.net.Uri
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu

interface BasePostBlockWidgetListener

interface AttachmentWidgetListener : BasePostBlockWidgetListener

interface EmbedImageWidgetListener : BasePostBlockWidgetListener{

    fun onImageClicked(imageUri: Uri)
}

interface EmbedVideoWidgetListener: BasePostBlockWidgetListener

interface EmbedWebsiteWidgetListener: BasePostBlockWidgetListener, LinkListener

interface ParagraphWidgetListener: BasePostBlockWidgetListener, LinkListener{

    fun onUserClicked(userId: String)
}

interface MenuListener {
    fun onMenuClicked(postMenu: PostMenu)
}

interface LinkListener{

    fun onLinkClicked(linkUri: Uri)
}
