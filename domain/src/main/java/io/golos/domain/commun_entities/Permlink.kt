package io.golos.domain.commun_entities

import android.os.Parcelable
import io.golos.utils.id.IdUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
class Permlink constructor(val value: String): Parcelable {
    companion object {
        fun generate(): Permlink = Permlink("pl${IdUtil.generateLongId()}")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as Permlink

        if (value != other.value) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}