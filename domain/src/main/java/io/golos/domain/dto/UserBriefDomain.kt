package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserBriefDomain(
    val avatarUrl: String?,
    val userId: UserIdDomain,
    val username: String?
) : Parcelable