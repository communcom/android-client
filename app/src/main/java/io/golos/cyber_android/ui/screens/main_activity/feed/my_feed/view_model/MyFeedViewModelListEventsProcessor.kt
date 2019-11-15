package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostImageListener
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostLinkListener
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostUserListener
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostVoteListener

interface MyFeedViewModelListEventsProcessor : PostLinkListener,
    PostImageListener,
    PostUserListener,
    PostVoteListener,
    PostCommentsListener

interface PostCommentsListener {

    fun onCommentsClicked(postContentId: Post.ContentId)
}