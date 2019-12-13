package io.golos.cyber_android.ui.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentId(
    val communityId: String,
    val permlink: String,
    val userId: String
): Parcelable