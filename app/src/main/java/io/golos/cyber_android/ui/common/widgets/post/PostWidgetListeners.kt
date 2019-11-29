package io.golos.cyber_android.ui.common.widgets.post

import android.net.Uri
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu

interface BasePostBlockWidgetListener

interface RichWidgetListener: BasePostBlockWidgetListener{

    fun onLinkClicked(linkUri: Uri)
}

interface EmbedWidgetListener: BasePostBlockWidgetListener{

    fun onLinkClicked(linkUri: Uri)

}

interface AttachmentWidgetListener : BasePostBlockWidgetListener

interface EmbedImageWidgetListener : BasePostBlockWidgetListener {

    fun onImageClicked(imageUri: Uri)
}

interface EmbedVideoWidgetListener: BasePostBlockWidgetListener

interface EmbedWebsiteWidgetListener: BasePostBlockWidgetListener,
    LinkListener

interface ParagraphWidgetListener: BasePostBlockWidgetListener,
    LinkListener {

    fun onUserClicked(userId: String)
}

interface MenuListener {
    fun onMenuClicked(postMenu: PostMenu)
}

interface LinkListener{

    fun onLinkClicked(linkUri: Uri)
}
