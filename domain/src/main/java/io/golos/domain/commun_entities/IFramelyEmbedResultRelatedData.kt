package io.golos.domain.mappers

import io.golos.commun4j.services.model.IFramelyEmbedResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */

data class IFramelyEmbedResultRelatedData(val iframelyResult: IFramelyEmbedResult, val originalRequestUrl: String)