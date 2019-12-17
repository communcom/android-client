package io.golos.cyber_android.ui.screens.profile_comments.model.item

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Comment
import io.golos.domain.dto.CommentDomain
import io.golos.domain.utils.IdUtil

data class ProfileCommentListItem(
    val comment: Comment,
    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0
) : VersionedListItem