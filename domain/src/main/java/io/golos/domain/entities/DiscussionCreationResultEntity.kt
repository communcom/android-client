package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreationResultEntity : Entity

data class PostCreationResultEntity(val postId: DiscussionIdEntity):DiscussionCreationResultEntity()

data class CommentCreationResultEntity(val commentId: DiscussionIdEntity, val parentId: DiscussionIdEntity):DiscussionCreationResultEntity()