package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */


sealed class DiscussionCreationResultModel : Model

data class PostCreationResultModel(val postId: DiscussionIdModel) : DiscussionCreationResultModel() {
    companion object {
        val empty = PostCreationResultModel(DiscussionIdModel("#mpty#", "#empty#"))
    }
}

data class CommentCreationResultModel(val commentId: DiscussionIdModel, val parentId: DiscussionIdModel) :
    DiscussionCreationResultModel() {
    companion object {
        val empty = CommentCreationResultModel(
            DiscussionIdModel("#mpty#", "#empty#"),
            DiscussionIdModel("#mpty#", "#empty#")
        )
    }
}

data class UpdatePostResultModel(val postId: DiscussionIdModel) : DiscussionCreationResultModel()

data class DeleteDiscussionResultModel(val postId: DiscussionIdModel) : DiscussionCreationResultModel()
