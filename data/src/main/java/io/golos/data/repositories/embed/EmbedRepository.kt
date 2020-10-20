package io.golos.data.repositories.embed

import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult

interface EmbedRepository {
    suspend fun getIframelyEmbed(url: String): IFramelyEmbedResult

    suspend fun getOEmbedEmbed(url: String): OEmbedResult?
}