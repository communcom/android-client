package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.PostBodyListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.PostControlsListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.PostTitleListItem
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.utils.IdUtil
import javax.inject.Inject

/**
 * Contains list with post data
 */
class PostListDataSourceImpl
@Inject
constructor(
) : PostListDataSource {

    private val postList = mutableListOf<VersionedListItem>()

    override fun init(postModel: PostModel): List<VersionedListItem> {
        createOrUpdateTitle(postModel)
        createOrUpdateBody(postModel)
        createOrUpdatePostControls(postModel)

        return postList
    }

    private fun createOrUpdateTitle(postModel: PostModel) {
        val oldTitle = postList.singleOrNull { it is PostTitleListItem }

        val newTitle = postModel.content.body.postBlock.title?.let {
            PostTitleListItem(IdUtil.generateLongId(), 0, it)
        }

        when {
            oldTitle == null && newTitle == null -> {}
            oldTitle == null && newTitle != null -> postList.add(0, newTitle)
            oldTitle != null && newTitle == null -> postList.remove(oldTitle)
            oldTitle != null && newTitle != null ->
                postList[0] = (oldTitle as PostTitleListItem).copy(version = oldTitle.version + 1, title = newTitle.title)
        }
    }

    private fun createOrUpdateBody(postModel: PostModel) {
        val oldBodyIndex = postList.indexOfFirst { it is PostBodyListItem }

        if(oldBodyIndex == -1) {
            postList.add(PostBodyListItem(IdUtil.generateLongId(), 0, postModel.content.body.postBlock))
        } else {
            val oldBody = postList[oldBodyIndex]
            postList[oldBodyIndex] = PostBodyListItem(oldBody.id, oldBody.version + 1, postModel.content.body.postBlock)
        }
    }

    private fun createOrUpdatePostControls(postModel: PostModel) {
        val oldControlsIndex = postList.indexOfFirst { it is PostControlsListItem }

        val controls = PostControlsListItem(
            IdUtil.generateLongId(),
            version = 0,
            voteBalance = postModel.votes.upCount - postModel.votes.downCount,
            isUpVoteActive = false,
            isDownVoteActive = false,
            totalComments = postModel.comments.count,
            totalViews = postModel.stats.viewsCount
        )

        if(oldControlsIndex == -1) {
            postList.add(controls)
        } else {
            val oldControls = postList[oldControlsIndex]
            postList[oldControlsIndex] = controls.copy(id = oldControls.id, version = oldControls.version + 1)
        }
    }
}