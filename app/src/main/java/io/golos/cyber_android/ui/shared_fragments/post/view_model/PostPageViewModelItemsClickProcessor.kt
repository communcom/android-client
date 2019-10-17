package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri

/**
 * Processing post items clicks
 */
interface PostPageViewModelItemsClickProcessor {
    fun onImageInPostClick(imageUri: Uri)

    fun onLinkInPostClick(link: Uri)

    fun onUserInPostClick(userName: String)

    fun onUpVoteClick()

    fun onDownVoteClick()
}