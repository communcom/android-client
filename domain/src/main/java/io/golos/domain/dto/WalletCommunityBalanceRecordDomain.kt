package io.golos.domain.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WalletCommunityBalanceRecordDomain (
    val points: Double,
    val frozenPoints: Double?,

    val communityLogoUrl: String?,
    val communityName: String?,
    val communityId: CommunityIdDomain,

    /**
     * How many points we can buy for one Commun
     */
    val communs: Double?,

    val transferFee: Int?
): Parcelable