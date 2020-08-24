package io.golos.cyber_android.ui.screens.discovery.model

import androidx.lifecycle.LiveData
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.repositories.DiscoveryRepository
import javax.inject.Inject

class DiscoveryModelImpl
@Inject
constructor(
    private val discoveryRepository: DiscoveryRepository
) : DiscoveryModel {

    override suspend fun search(search: String) = discoveryRepository.getSearchResult(search)

    override fun getSearchedCommunities(): LiveData<CommunityDomain> {
        TODO("Not yet implemented")
    }

}