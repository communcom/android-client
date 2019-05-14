package io.golos.domain.requestmodel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
data class EmbedRequest(val url: String) : Identifiable {
    private val _id = Id(url)
    override val id: Identifiable.Id
        get() = _id

    inner class Id(val url: String) : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (url != other.url) return false

            return true
        }

        override fun hashCode(): Int {
            return url.hashCode()
        }
    }

}
