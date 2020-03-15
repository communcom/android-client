package io.golos.cyber_android.ui.dto

import android.os.Parcelable
import io.golos.domain.dto.CommunityIdDomain
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentId(
    val communityId: CommunityIdDomain,
    val permlink: String,
    val userId: String
): Parcelable