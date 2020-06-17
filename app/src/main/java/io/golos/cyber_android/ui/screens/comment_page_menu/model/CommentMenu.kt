package io.golos.cyber_android.ui.screens.comment_page_menu.model

import android.os.Parcelable
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CommentMenu(
    val communityId: CommunityIdDomain,
    val communityName: String?,
    val communityAvatarUrl: String?,
    val contentId: ContentIdDomain?,
    val creationTime: Date,
    val authorUsername: String?,
    val authorUserId: String,
    val authorAvatarUrl: String?,
    val permlink: String
) : Parcelable