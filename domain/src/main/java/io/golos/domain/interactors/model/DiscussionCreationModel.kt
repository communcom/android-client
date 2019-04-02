package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreationRequest : Model

data class PostCreationRequestModel(
    val title: String,
    val body: String,
    val tags: List<String>
) : DiscussionCreationRequest()

data class CommentCreationRequestModel(
    val body: String,
    val parentId: DiscussionIdModel,
    val tags: List<String>
) : DiscussionCreationRequest()
