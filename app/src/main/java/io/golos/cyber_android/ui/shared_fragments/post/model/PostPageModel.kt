package io.golos.cyber_android.ui.shared_fragments.post.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel

interface PostPageModel : ModelBase {
    suspend fun getPost(): PostModel

    fun getPostHeader(post: PostModel): PostHeader

    suspend fun getUserId(userName: String): String

    suspend fun deletePost(postId: DiscussionIdModel)
}