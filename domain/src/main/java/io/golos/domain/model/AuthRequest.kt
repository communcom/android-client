package io.golos.domain.model

import io.golos.domain.entities.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
class AuthRequest(val user: CyberUser, val activeKey: String) : Identifiable {

    inner class Id(val user: CyberUser, val activeKey: String) : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (user != other.user) return false
            if (activeKey != other.activeKey) return false

            return true
        }

        override fun hashCode(): Int {
            var result = user.hashCode()
            result = 31 * result + activeKey.hashCode()
            return result
        }
    }

    override val id: Identifiable.Id
            by lazy { Id(user, activeKey) }
}