package io.golos.data.repositories.embed

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.repositories.RepositoryBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class EmbedRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    EmbedRepository {

    override suspend fun getIframelyEmbed(url: String): IFramelyEmbedResult = apiCall { commun4j.getIframelyEmbed(url) }

    override suspend fun getOEmbedEmbed(url: String): OEmbedResult? = apiCall { commun4j.getOEmdedEmbed(url) }
}