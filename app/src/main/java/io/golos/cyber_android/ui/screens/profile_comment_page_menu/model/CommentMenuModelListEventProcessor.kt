package io.golos.cyber_android.ui.screens.profile_comment_page_menu.model

interface CommentMenuModelListEventProcessor : CommentMenuListener

interface CommentMenuListener {

    fun onEditCommentEvent()

    fun onDeleteCommentEvent()

}