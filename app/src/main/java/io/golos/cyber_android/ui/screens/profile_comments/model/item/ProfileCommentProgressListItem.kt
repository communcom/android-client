package io.golos.cyber_android.ui.screens.profile_comments.model.item

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil

class ProfileCommentProgressListItem(
    override val version: Long = 0,
    override val id: Long = IdUtil.generateLongId(),
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false
) : VersionedListItem