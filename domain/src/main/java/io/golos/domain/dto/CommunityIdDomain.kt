package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Community id is: 1) its code OR its alias
 */
@Parcelize
data class CommunityIdDomain(
    val code: String?,
    val alias: String?
): Parcelable