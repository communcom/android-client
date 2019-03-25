package io.golos.domain.interactors.feed

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.rules.EntityToModelMapper

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedUseCase(
    private val communityId: CommunityId,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: EntityToModelMapper<FeedRelatedEntities, PostFeed>,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<CommunityFeedUpdateRequest>(
    postFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {


    override val baseFeedUpdateRequest: CommunityFeedUpdateRequest
            by lazy { CommunityFeedUpdateRequest(communityId.id, 0, DiscussionsSort.FROM_NEW_TO_OLD, null) }

    override fun requestFeedUpdate(limit: Int, option: UpdateOption) {
        val myFeed = postFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
        val nextPageId = myFeed?.nextPageId
        val resolvedOption = option.resolveUpdateOption()
        val request = CommunityFeedUpdateRequest(
            communityId.id,
            limit,
            DiscussionsSort.FROM_NEW_TO_OLD,
            when (resolvedOption) {
                UpdateOption.REFRESH_FROM_BEGINNING -> null
                UpdateOption.FETCH_NEXT_PAGE -> nextPageId
            }
        )
        postFeedRepository.makeAction(request)
    }
}