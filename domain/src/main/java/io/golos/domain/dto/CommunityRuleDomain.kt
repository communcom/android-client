package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityRuleDomain (
    val title: String?,
    val text: String?
): Parcelable