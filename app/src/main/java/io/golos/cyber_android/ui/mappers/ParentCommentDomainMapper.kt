package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.ParentComment
import io.golos.domain.dto.ParentCommentDomain

fun ParentCommentDomain.mapToParentComment(): ParentComment {
    return ParentComment(this.comment, this.post)
}