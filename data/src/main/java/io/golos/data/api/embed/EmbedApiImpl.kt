package io.golos.data.api.embed

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import javax.inject.Inject

class EmbedApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), EmbedApi {

    override fun getIframelyEmbed(url: String): IFramelyEmbedResult = commun4j.getEmbedIframely(url).getOrThrow()

    override fun getOEmbedEmbed(url: String): OEmbedResult = commun4j.getEmbedOembed(url).getOrThrow()
}