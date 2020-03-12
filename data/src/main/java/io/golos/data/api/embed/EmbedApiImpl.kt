package io.golos.data.api.embed

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class EmbedApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), EmbedApi {

    override fun getIframelyEmbed(url: String): IFramelyEmbedResult {
        return commun4j.getIframelyEmbed(url).getOrThrow()
    }

    override fun getOEmbedEmbed(url: String): OEmbedResult?{
        return commun4j.getOEmdedEmbed(url).getOrThrow()
    }
}