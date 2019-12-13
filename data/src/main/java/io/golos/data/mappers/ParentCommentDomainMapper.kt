package io.golos.data.mappers

import io.golos.commun4j.services.model.ParentComment
import io.golos.domain.dto.ParentCommentIdentifierDomain

fun ParentCommentIdentifierDomain.mapToParentComment(): ParentComment{
    return ParentComment(userIdDomain.mapToCyberName(), this.permlink)
}