package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.*
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscoveryRepository
import javax.inject.Inject

class DiscoveryRepositoryImpl
@Inject
constructor(
    private val callProxy: NetworkCallProxy,
    private val commun4j: Commun4j
) :DiscoveryRepository {

    override suspend fun getSearchResult(searchString: String): ExtendedSearchResponse {
        val searchResult = callProxy.call { commun4j.extendedSearch(searchString,
            ExtendedRequestSearchItem(20,0),
            ExtendedRequestSearchItem(20,0),
            ExtendedRequestSearchItem(20,0)
            )
        }

        return searchResult
    }
}