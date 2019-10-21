package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionMetadata
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.interactors.model.DiscussionMetadataModel

fun DiscussionMetadata.map(): DiscussionMetadataModel =
    DiscussionMetadataModel(this.creationTime, this.creationTime.asElapsedTime())