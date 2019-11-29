package io.golos.cyber_android.ui.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Community (
    val communityId: String,

    val name: String,

    val avatarUrl: String?,
    val coverUrl: String?,

    val subscribersCount: Int,
    val postsCount: Int,
    val isSubscribed: Boolean
): Parcelable