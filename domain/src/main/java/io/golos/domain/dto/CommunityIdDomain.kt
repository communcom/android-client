package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityIdDomain(
    val code: String
): Parcelable