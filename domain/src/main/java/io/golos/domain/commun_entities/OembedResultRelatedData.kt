package io.golos.domain.mappers

import io.golos.commun4j.services.model.OEmbedResult

data class OembedResultRelatedData(val oembedResult: OEmbedResult, val originalRequestUrl: String)