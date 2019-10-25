package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.posted_comments_collection

import io.golos.domain.interactors.model.DiscussionIdModel
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class PostedCommentsCollectionImpl
@Inject
constructor(): PostedCommentsCollection {

    private val entities = ConcurrentHashMap<DiscussionIdModel, DiscussionIdModel>()

    override fun addEntity(id: DiscussionIdModel) {
        entities[id] = id
    }

    override fun isEntityExists(id: DiscussionIdModel): Boolean = entities[id] != null
}