package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.services.model.CommunitiesRequestType
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.mappers.*
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunitiesRepositoryImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorageFacade: KeyValueStorageFacade
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    CommunitiesRepository {

    override fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>) {
        keyValueStorageFacade.saveFtueCommunitySubscriptions(communitySubscriptions.mapToCommunityEntityList())
    }

    override suspend fun getCommunitySubscriptions(): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher){
            keyValueStorageFacade.getFtueCommunitySubscriptions().mapToCommunityDomainList()
        }
    }

    override suspend fun sendCommunitiesCollection(communityIds: List<String>) {
        apiCall{
            commun4j.onBoardingCommunitySubscriptions(CyberName(currentUserRepository.userId.userId), communityIds)
        }
    }

    override suspend fun getCommunityById(communityId: CommunityIdDomain): CommunityPageDomain {
        val community = apiCall { commun4j.getCommunity(communityId.code) }

        val leads = apiCall { commun4j.getLeaders(community.communityId, 50, 0) }.items.map { it.userId }
        return community.mapToCommunityPageDomain(leads)
    }

    override suspend fun getCommunityIdByAlias(alias: String): CommunityIdDomain {
        return  when(alias) {
            "cats" -> CommunityIdDomain("CAT")
            "art" -> CommunityIdDomain("ART")
            else -> CommunityIdDomain("CAT")        // TEMPORARY FOR DEBUG PURPOSE
        }
    }

    override suspend fun subscribeToCommunity(communityId: CommunityIdDomain) {
        apiCallChain {
            commun4j.followCommunity(
                communityCode = CyberSymbolCode(communityId.code),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                follower = CyberName(currentUserRepository.authState!!.user.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain) {
        apiCallChain {
            commun4j.unFollowCommunity(
                communityCode = CyberSymbolCode(communityId.code),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                follower = CyberName(currentUserRepository.authState!!.user.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getCommunitiesByQuery(query, offset, pageLimitSize)
        }
    }

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getRecommendedCommunities(offset, pageLimitSize)
        }
    }

    override suspend fun getCommunitiesList(
        userId: UserIdDomain,
        offset: Int,
        pageSize: Int,
        showAll: Boolean,
        searchQuery: String?
    ): List<CommunityDomain> {
        return apiCall {
            commun4j.getCommunitiesList(
                type = if(showAll) CommunitiesRequestType.ALL else CommunitiesRequestType.USER,
                userId = CyberName(userId.userId),
                search = searchQuery,
                offset = offset,
                limit = pageSize)
            }
            .items
            .map { it.mapToCommunityDomain() }
    }

    override suspend fun getCommunityLeads(communityId: CommunityIdDomain): List<CommunityLeaderDomain> =
        apiCall { commun4j.getLeaders(communityId.code, 50, 0) }
            .items
            .map { it.mapToCommunityLeaderDomain() }

    override suspend fun getCommunitiesInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<CommunityDomain> =
        apiCall { commun4j.getBlacklistedCommunities(CyberName(userId.userId)) }.items.map { it.mapToCommunityDomain() }

    override suspend fun moveCommunityToBlackList(communityId: CommunityIdDomain) {
        apiCallChain {
            commun4j.hide(
                communCode = CyberSymbolCode(communityId.code),
                user = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun moveCommunityFromBlackList(communityId: CommunityIdDomain) {
        apiCallChain {
            commun4j.unHide(
                communCode = CyberSymbolCode(communityId.code),
                user = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun voteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain) {
        apiCallChain {
            commun4j.voteLeader(
                communCode = CyberSymbolCode(communityId.code),
                leader = CyberName(leader.userId),
                pct = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                voter = CyberName(currentUserRepository.userId.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun unvoteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain) {
        apiCallChain {
            commun4j.unVoteLeader(
                communCode = CyberSymbolCode(communityId.code),
                leader = CyberName(leader.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                voter = CyberName(currentUserRepository.userId.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
            )
        }
    }

    override suspend fun getSubscribers(communityId: CommunityIdDomain, offset: Int, pageSizeLimit: Int): List<UserDomain> {
        return apiCall {
            commun4j.getSubscribers(
                null,
                communityId.code,
                pageSizeLimit,
                offset
            )
        }.items.map { it.mapToUserDomain() }
    }

    override suspend fun getUserCommunities(userIdDomain: UserIdDomain, offset: Int, pageSizeLimit: Int): List<CommunityDomain> =
        apiCall { commun4j.getCommunitySubscriptions(CyberName(userIdDomain.userId), pageSizeLimit, offset) }
        .map { it.mapToCommunityDomain() }
}