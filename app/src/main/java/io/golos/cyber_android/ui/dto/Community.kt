package io.golos.cyber_android.ui.dto

import android.os.Parcelable
import io.golos.domain.dto.CommunityIdDomain
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Community (
    val communityId: CommunityIdDomain,
    val alias: String?,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val subscribersCount: Int,
    val postsCount: Int,
    var isSubscribed: Boolean
): Parcelable