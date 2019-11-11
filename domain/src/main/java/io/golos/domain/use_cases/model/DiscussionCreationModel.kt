package io.golos.domain.use_cases.model

import io.golos.domain.Model
import io.golos.domain.commun_entities.Permlink

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreationRequest : Model

data class PostCreationRequestModel(
    val title: String,
    val body: CharSequence,//in real app use only spanned
    val tags: List<String>,
    val images: List<String> = emptyList()
) : DiscussionCreationRequest()

data class CommentCreationRequestModel(
    val body: String,//in real app use only spanned
    val parentId: DiscussionIdModel,
    val tags: List<String>
) : DiscussionCreationRequest()

data class UpdatePostRequestModel(
    val permlink: Permlink,
    val title: String,
    val body: CharSequence,//in real app use only spanned
    val tags: List<String>,
    val images: List<String> = emptyList()
) : DiscussionCreationRequest()