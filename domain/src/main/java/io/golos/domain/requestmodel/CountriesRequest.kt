package io.golos.domain.requestmodel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesRequest : Identifiable {
    private val _id = Id()
    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id()
}