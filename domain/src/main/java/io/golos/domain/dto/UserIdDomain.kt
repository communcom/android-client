package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserIdDomain(
    val userId: String
) : Parcelable