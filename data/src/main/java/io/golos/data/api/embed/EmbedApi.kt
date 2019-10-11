package io.golos.data.api.embed

import io.golos.commun4j.services.model.IFramelyEmbedResult
import io.golos.commun4j.services.model.OEmbedResult

interface EmbedApi {
    fun getIframelyEmbed(url: String): IFramelyEmbedResult

    fun getOEmbedEmbed(url: String): OEmbedResult
}