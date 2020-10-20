package io.golos.cyber_android.ui.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileCommunities(
    val communitiesSubscribedCount: Int,
    val highlightCommunities: List<Community>
) : Parcelable