package io.golos.cyber_android.ui.screens.post_page_menu.model

interface PostMenuModelListEventProcessor: PostMenuListener

interface PostMenuListener {

    fun onAddToFavoriteItemClick()

    fun onRemoveFromFavoriteItemClick()

    fun onShareItemClick(shareUrl: String)

    fun onEditItemClick()

    fun onDeleteItemClick()

    fun onJoinItemClick(communityId: String)

    fun onJoinedItemClick(communityId: String)

    fun onReportItemClick()

}