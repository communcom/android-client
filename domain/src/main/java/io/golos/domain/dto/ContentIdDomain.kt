package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentIdDomain(
    val communityId: CommunityIdDomain,
    val permlink: String,
    val userId: UserIdDomain
): Parcelable