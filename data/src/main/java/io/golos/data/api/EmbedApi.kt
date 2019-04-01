package io.golos.data.api

import io.golos.cyber4j.model.IFramelyEmbedResult
import io.golos.cyber4j.model.OEmbedResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
interface EmbedApi {

    fun getIframelyEmbed(url: String):IFramelyEmbedResult

    fun getOEmbedEmbed(url: String):OEmbedResult
}