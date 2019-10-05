package io.golos.domain.interactors.feed

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.CommunityFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.mappers.EntityToModelMapper
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedUseCase
@Inject
constructor(
    private val communityId: CommunityId,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>>,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>(
    postFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {


    override val baseFeedUpdateRequest: CommunityFeedUpdateRequest
            by lazy { CommunityFeedUpdateRequest(communityId.id, 0, DiscussionsSort.FROM_NEW_TO_OLD,
                FeedTimeFrameOption.ALL, null) }

    override fun requestFeedUpdate(limit: Int, option: UpdateOption,
                                   sort: DiscussionsSort?,
                                   timeFrame: FeedTimeFrameOption?) {
        val myFeed = discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
        val nextPageId = myFeed?.nextPageId
        val resolvedOption = option.resolveUpdateOption()
        val request = CommunityFeedUpdateRequest(
            communityId.id,
            limit,
            sort ?: DiscussionsSort.FROM_NEW_TO_OLD,
            timeFrame ?: FeedTimeFrameOption.ALL,
            when (resolvedOption) {
                UpdateOption.REFRESH_FROM_BEGINNING -> null
                UpdateOption.FETCH_NEXT_PAGE -> nextPageId
            }
        )
        discussionsFeedRepository.makeAction(request)
    }
}