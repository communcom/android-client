package io.golos.domain.interactors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedUseCase(
    val community: CommunityModel,
    private val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>
) : UseCase<PostFeed> {
    private val postFeedLiveData = MutableLiveData<PostFeed>()
    private val mediatorLiveData = MediatorLiveData<Any>()
    private val basicRequest = CommunityFeedUpdateRequest(
        community.id,
        0,
        DiscussionsSort.FROM_NEW_TO_OLD, null
    )

    override val getAsLiveData: LiveData<PostFeed>
        get() = postFeedLiveData

    fun requestUpdate(limit: Int = 20) {

    }

    fun subscribe() {
        mediatorLiveData.addSource(
            postFeedRepository.getAsLiveData(basicRequest)
        ) { feedEntity: FeedEntity<PostEntity>? ->

        }
    }

    fun unsubscribe() {
        mediatorLiveData.removeSource(postFeedRepository.getAsLiveData(basicRequest))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommunityFeedUseCase

        if (community != other.community) return false

        return true
    }

    override fun hashCode(): Int {
        return community.hashCode()
    }

    override fun toString(): String {
        return "CommunityFeedUseCase(community=$community)"
    }


}