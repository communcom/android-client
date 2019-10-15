package io.golos.cyber_android.ui.shared_fragments.post.dto

import io.golos.domain.commun_entities.Community
import java.util.*

data class PostHeader(
    val community: Community,
    val userName: String,
    val actionDateTime: Date,

    val canJoinToCommunity: Boolean,
    val canEdit: Boolean
)