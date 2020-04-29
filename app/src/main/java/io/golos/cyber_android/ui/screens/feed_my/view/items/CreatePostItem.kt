package io.golos.cyber_android.ui.screens.feed_my.view.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.widgets.EditorWidget
import kotlinx.android.synthetic.main.item_create_post.view.*

class CreatePostItem(
    val user: User?,
    private val onCreatePostClick: (() -> Unit)?,
    private val onUserClick: (() -> Unit)?) : BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_editor_widget

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        user?.let {
            val widget = (view.editorWidget as EditorWidget)
            widget.loadUserAvatar(user.avatarUrl, user.userName)

            widget.listener = object: EditorWidget.Listener {
                override fun onGalleryClick() {
                    onCreatePostClick?.invoke()
                }
                override fun onCaptionClick() {
                    onCreatePostClick?.invoke()
                }
                override fun onAvatarClick() {
                    onUserClick?.invoke()
                }
            }
        }
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)

        val widget = (view.editorWidget as EditorWidget)
        widget.clearUserAvatar()
        widget.listener = null
    }

    override fun areItemsTheSame(): Int = user?.id.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is CreatePostItem) {
            return user == item.user
        }
        return false
    }
}