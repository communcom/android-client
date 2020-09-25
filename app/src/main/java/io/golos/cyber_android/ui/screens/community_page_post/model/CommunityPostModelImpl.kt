package io.golos.cyber_android.ui.screens.community_page_post.model

import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunityPostModelImpl
@Inject
constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val walletRepository: WalletRepository,
    private val globalSettingsRepository: GlobalSettingsRepository
) : CommunityPostModel,
    GetPostsUseCase by getPostsUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override val rewardCurrency: RewardCurrency
        get() = globalSettingsRepository.rewardCurrency

    override val rewardCurrencyUpdates: Flow<RewardCurrency?>
        get() = globalSettingsRepository.rewardCurrencyUpdates

    override suspend fun addToFavorite(permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
        }
    }

    override suspend fun removeFromFavorite(permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
        }
    }

    override suspend fun deletePost(permlink: String, communityId: CommunityIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePost(permlink, communityId)
        }
    }

    override suspend fun upVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(
                ContentIdDomain(
                    communityId = communityId,
                    permlink = permlink,
                    userId = userId
                )
            )
        }
    }

    override suspend fun unVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.unVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
        }
    }

    override suspend fun downVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(
                ContentIdDomain(
                    communityId = communityId,
                    permlink = permlink,
                    userId = userId
                )
            )
        }
    }

    override suspend fun reportPost(authorPostId: UserIdDomain, communityId: CommunityIdDomain, permlink: String, reason: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.reportPost(communityId, authorPostId, permlink, reason)
        }
    }

    override suspend fun getWalletBalance(): List<WalletCommunityBalanceRecordDomain> = walletRepository.getBalance()

    override suspend fun updateRewardCurrency(currency: RewardCurrency) = globalSettingsRepository.updateRewardCurrency(currency)
}