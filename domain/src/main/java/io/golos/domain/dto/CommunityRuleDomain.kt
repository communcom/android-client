package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityRuleDomain (
    val index: Int,         // From 0
    val title: String?,
    val text: String?
): Parcelable