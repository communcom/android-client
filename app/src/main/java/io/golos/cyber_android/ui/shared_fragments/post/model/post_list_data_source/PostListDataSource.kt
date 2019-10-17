package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.interactors.model.PostModel

interface PostListDataSource {
    /**
     * Creates initial data
     */
    fun init(postModel: PostModel) : List<VersionedListItem>
}