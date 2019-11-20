package io.golos.cyber_android.ui.dialogs.post.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PostMenu(
    val communityId: String,
    val communityName: String?,
    val communityAvatarUrl: String?,
    val creationTime: Date,
    val authorUsername: String?,
    val authorUserId: String,
    val shareUrl: String?,
    val isMyPost: Boolean,
    val isSubscribe: Boolean
): Parcelable