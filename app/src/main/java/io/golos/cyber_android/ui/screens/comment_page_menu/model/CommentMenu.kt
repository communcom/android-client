package io.golos.cyber_android.ui.screens.comment_page_menu.model

import android.os.Parcelable
import io.golos.cyber_android.ui.dto.ContentId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CommentMenu(
    val communityId: String,
    val communityName: String?,
    val communityAvatarUrl: String?,
    val contentId: ContentId?,
    val creationTime: Date,
    val authorUsername: String?,
    val authorUserId: String,
    val permlink: String
) : Parcelable