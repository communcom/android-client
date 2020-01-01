package io.golos.cyber_android.ui.screens.profile_comments.model.item

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Comment
import io.golos.domain.utils.IdUtil

data class ProfileCommentListItem(
    var comment: Comment,
    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0
) : VersionedListItem