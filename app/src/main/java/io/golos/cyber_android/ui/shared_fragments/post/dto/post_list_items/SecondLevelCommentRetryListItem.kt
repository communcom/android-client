package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.model.DiscussionIdModel

data class SecondLevelCommentRetryListItem(
    override val id: Long,
    override val version: Long,

    val parentCommentId: DiscussionIdModel
) : VersionedListItem