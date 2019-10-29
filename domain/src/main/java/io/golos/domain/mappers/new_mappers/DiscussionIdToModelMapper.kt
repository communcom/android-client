package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.interactors.model.DiscussionIdModel

fun DiscussionId.map(): DiscussionIdModel = DiscussionIdModel(this.userId.name, Permlink(this.permlink))
