package io.golos.domain.requestmodel

import io.golos.domain.entities.AuthType
import io.golos.domain.entities.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
open class AuthRequest(
    val user: CyberUser,
    val activeKey: String,
    val type: AuthType
) : Identifiable {

    inner class Id(val user: CyberUser) : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (user != other.user) return false


            return true
        }

        override fun hashCode(): Int {
            return user.hashCode()
        }
    }

    override val id: Identifiable.Id
            by lazy { Id(user) }
}

data class AuthRequestModel(
    val user: CyberUser,
    val activeKey: String,
    val type: AuthType)