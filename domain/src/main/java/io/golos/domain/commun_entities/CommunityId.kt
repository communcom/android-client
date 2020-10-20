package io.golos.domain.commun_entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityId(val id: String): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as CommunityId

        if (id != other.id) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}