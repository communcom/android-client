package io.golos.data.repositories.embed

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.data.repositories.network_call.NetworkCallProxy
import javax.inject.Inject

class EmbedRepositoryImpl
@Inject
constructor(
    private val callProxy: NetworkCallProxy,
    private val commun4j: Commun4j
) : EmbedRepository {

    override suspend fun getIframelyEmbed(url: String): IFramelyEmbedResult = callProxy.call { commun4j.getIframelyEmbed(url) }

    override suspend fun getOEmbedEmbed(url: String): OEmbedResult? = callProxy.call { commun4j.getOEmdedEmbed(url) }
}