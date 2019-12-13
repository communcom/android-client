package io.golos.data.mappers

import io.golos.commun4j.services.model.CommentsSortType
import io.golos.domain.dto.CommentDomain

fun CommentDomain.CommentTypeDomain.mapToCommentSortType(): CommentsSortType{
    return when(this){
        CommentDomain.CommentTypeDomain.POST -> CommentsSortType.POST
        CommentDomain.CommentTypeDomain.REPLIES -> CommentsSortType.REPLIES
        CommentDomain.CommentTypeDomain.USER -> CommentsSortType.USER
    }
}