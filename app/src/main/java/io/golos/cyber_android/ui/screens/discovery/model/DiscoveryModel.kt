package io.golos.cyber_android.ui.screens.discovery.model

import androidx.lifecycle.LiveData
import io.golos.commun4j.services.model.ExtendedSearchResponse
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityDomain

interface DiscoveryModel : ModelBase {

    suspend fun search(search: String): ExtendedSearchResponse

    fun getSearchedCommunities(): LiveData<CommunityDomain>

}