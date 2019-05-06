package io.golos.domain.requestmodel

import io.golos.cyber4j.model.CyberName
import io.golos.domain.entities.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
sealed class UserMetadataRequest : Identifiable

data class UserMetadataFetchRequest(val user: CyberName) : UserMetadataRequest() {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _user = user
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_user != other._user) return false

            return true
        }

        override fun hashCode(): Int {
            return _user.hashCode()
        }


    }
}