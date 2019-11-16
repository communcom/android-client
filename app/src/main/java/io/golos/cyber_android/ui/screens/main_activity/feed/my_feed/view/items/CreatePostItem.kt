package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.widgets.EditorWidget
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model.MyFeedListListener
import kotlinx.android.synthetic.main.item_create_post.view.*

class CreatePostItem(private val user: User?, private val eventsProcessor: MyFeedListListener) : BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_editor_widget

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        user?.let {
            (view.editorWidget as EditorWidget).loadUserAvatar(user.avatarUrl, user.userName)
        }
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)
        (view.editorWidget as EditorWidget).findViewById<EditorWidget>(R.id.editorWidget).clearUserAvater()
    }

}