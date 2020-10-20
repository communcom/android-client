package io.golos.data.mappers

import io.golos.commun4j.services.model.CyberCommentParent
import io.golos.domain.dto.ParentCommentDomain

fun CyberCommentParent.mapToParentCommentDomain(): ParentCommentDomain{
    return ParentCommentDomain(this.comment?.mapToContentIdDomain(), this.post?.mapToContentIdDomain())
}