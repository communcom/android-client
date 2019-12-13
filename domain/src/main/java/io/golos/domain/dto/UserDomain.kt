package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDomain(
        val userId: UserIdDomain,
        val userName: String,
        val userAvatar: String?,
        val postsCount: Int?,
        val followersCount: Int?
): Parcelable