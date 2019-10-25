package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.posted_comments_collection

import io.golos.domain.interactors.model.DiscussionIdModel

interface PostedCommentsCollectionRead {
    fun isEntityExists(id: DiscussionIdModel): Boolean
}