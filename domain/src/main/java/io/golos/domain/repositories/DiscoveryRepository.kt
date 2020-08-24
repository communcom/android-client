package io.golos.domain.repositories

import io.golos.commun4j.services.model.ExtendedSearchResponse

interface DiscoveryRepository {
    suspend fun getSearchResult(searchString:String):ExtendedSearchResponse
}