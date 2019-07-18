package io.golos.domain.interactors.model

import io.golos.domain.Model
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.CountryEntity
import io.golos.domain.rules.EntityToModelMapper
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
data class CountryModel(
    val countryPhoneCode: Int,
    val countryCode: String,
    val countryName: String,
    val thumbNailUrl: String
) : Model

data class CountriesListModel(val list: List<CountryModel>) : List<CountryModel> by list, Model

@ApplicationScope
class CountryEntityToModelMapper
@Inject
constructor() : EntityToModelMapper<CountryEntity, CountryModel> {
    private val cache = Collections.synchronizedMap(HashMap<CountryEntity, CountryModel>())
    override suspend fun invoke(entity: CountryEntity): CountryModel {
        return cache.getOrPut(entity) {
            CountryModel(entity.countryPhoneCode, entity.countryCode, entity.countryName, entity.thumbNailUrl)
        }
    }
}