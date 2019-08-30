package io.golos.domain.requestmodel

import io.golos.cyber4j.sharedmodel.CyberName

sealed class PushNotificationsStateUpdateRequest(val toEnable: Boolean): Identifiable {

    private val _id = Id()

    override val id = _id

    inner class Id: Identifiable.Id() {
        val _toEnable = toEnable
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_toEnable != other._toEnable) return false

            return true
        }

        override fun hashCode(): Int {
            val result = _toEnable.hashCode()
            return result
        }


    }

}


class PushNotificationsUnsubscribeRequest(val userId: CyberName): PushNotificationsStateUpdateRequest(false)

class PushNotificationsSubscribeRequest(): PushNotificationsStateUpdateRequest(true)