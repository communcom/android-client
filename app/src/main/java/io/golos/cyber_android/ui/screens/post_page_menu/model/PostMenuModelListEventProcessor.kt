package io.golos.cyber_android.ui.screens.post_page_menu.model

interface PostMenuModelListEventProcessor: PostMenuListener

interface PostMenuListener {

    fun onAddToFavoriteItemClick()

    fun onRemoveFromFavoriteItemClick()

    fun onShareItemClick()

    fun onEditItemClick()

    fun onDeleteItemClick()

    fun onJoinItemClick()

    fun onJoinedItemClick()

    fun onReportItemClick()

}