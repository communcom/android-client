package io.golos.domain.interactors.model

import android.os.Parcelable
import io.golos.domain.Model
import io.golos.domain.commun_entities.Permlink
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiscussionIdModel(
    val userId: String,
    val permlink: Permlink
) : Model, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as DiscussionIdModel

        if (userId != other.userId) {
            return false
        }

        if (permlink.value != other.permlink.value) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + permlink.hashCode()
        return result
    }
}
