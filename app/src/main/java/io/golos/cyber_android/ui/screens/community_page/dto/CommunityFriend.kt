package io.golos.cyber_android.ui.screens.community_page.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityFriend(
    val userId: String,
    val userName: String,
    val avatarUrl: String,
    val hasAward: Boolean
) : Parcelable
