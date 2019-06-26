package io.golos.domain.requestmodel

class PushNotificationsRequest(val toEnable: Boolean): Identifiable {

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
            return _toEnable.hashCode()
        }


    }

}