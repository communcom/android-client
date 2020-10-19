package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.mappers.*
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CommunitiesRepositoryImpl
@Inject constructor(private val callProxy: NetworkCallProxy,
    //    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider, private val commun4j: Commun4j, private val currentUserRepository: CurrentUserRepositoryRead, private val userKeyStore: UserKeyStore, private val keyValueStorageFacade: KeyValueStorageFacade) : CommunitiesRepository {

    override fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>) {
        keyValueStorageFacade.saveFtueCommunitySubscriptions(communitySubscriptions.mapToCommunityEntityList())
    }

    override suspend fun getCommunitySubscriptions(): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorageFacade.getFtueCommunitySubscriptions().mapToCommunityDomainList()
        }
    }

    override suspend fun sendCommunitiesCollection(communityIds: List<String>) {
        callProxy.call {
            Timber.tag("NET_SOCKET").d("CommunitiesRepositoryImpl::sendCommunitiesCollection(...)")
            commun4j.onBoardingCommunitySubscriptions(CyberName(currentUserRepository.userId.userId), communityIds)
        }
    }

    override suspend fun getCommunityById(communityId: CommunityIdDomain): CommunityPageDomain {
        Timber.tag("NET_SOCKET").d("CommunitiesRepositoryImpl::getCommunityById(...)")
        val community = callProxy.call { commun4j.getCommunity(communityId.code, null) }
        val leads = callProxy.call { commun4j.getLeaders(community.communityId, 50, 0) }.items.map { it.userId }
        val reportCount = callProxy.call { commun4j.getReports( listOf(communityId.code),
            ReportsRequestStatus.OPEN,
            ReportRequestContentType.POST,
            ReportsRequestTimeSort.TIME_DESC,
            40,
            0) }
        val proposalCount = callProxy.call { commun4j.getProposals( listOf(communityId.code), 40,0) }
        val proposalCounat = callProxy.call { commun4j.getProposals( listOf(communityId.code), 40,0) }
        return community.mapToCommunityPageDomain(leads,reportCount.size,proposalCount.size)
    }

    override suspend fun getCommunityIdByAlias(alias: String): CommunityIdDomain =
        CommunityIdDomain(callProxy.call { commun4j.getCommunity(null, alias) }.communityId)

    override suspend fun subscribeToCommunity(communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.followCommunity(communityCode = CyberSymbolCode(communityId.code), bandWidthRequest = BandWidthRequest.bandWidthFromComn, clientAuthRequest = ClientAuthRequest.empty, follower = CyberName(currentUserRepository.authState!!.user.userId), key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.unFollowCommunity(communityCode = CyberSymbolCode(communityId.code), bandWidthRequest = BandWidthRequest.bandWidthFromComn, clientAuthRequest = ClientAuthRequest.empty, follower = CyberName(currentUserRepository.authState!!.user.userId), key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        throw UnsupportedOperationException("")
        //        return withContext(dispatchersProvider.ioDispatcher) {
        //            communitiesApi.getCommunitiesByQuery(query, offset, pageLimitSize)
        //        }
    }

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        throw UnsupportedOperationException("")
        //        return withContext(dispatchersProvider.ioDispatcher) {
        //            communitiesApi.getRecommendedCommunities(offset, pageLimitSize)
        //        }
    }

    override suspend fun getCommunitiesList(userId: UserIdDomain, offset: Int, pageSize: Int, showAll: Boolean, searchQuery: String?): List<CommunityDomain> =
        if (!showAll && searchQuery.isNullOrBlank()) {
            getUserCommunities(userId, offset, pageSize)
        } else {
            callProxy.call {
                commun4j.getCommunitiesList(type = if (showAll) CommunitiesRequestType.ALL else CommunitiesRequestType.USER, userId = CyberName(userId.userId), search = searchQuery, offset = offset, limit = pageSize)
            }.items.map { it.mapToCommunityDomain() }
        }

    override suspend fun getFtueCommunitiesList(offset: Int, pageSize: Int, searchQuery: String?): List<CommunityDomain> {
        Timber.tag("NET_SOCKET").d("CommunitiesRepositoryImpl::getFtueCommunitiesList(offset: $offset; pageSize: $pageSize; searchQuery: $searchQuery)")
        return callProxy.call {
            commun4j.getCommunitiesList(type = CommunitiesRequestType.ALL, userId = null, search = searchQuery, offset = offset, limit = pageSize)
        }.items.map { it.mapToCommunityDomain() }
    }

    override suspend fun getCommunityLeads(communityId: CommunityIdDomain): List<CommunityLeaderDomain> =
        callProxy.call { commun4j.getLeaders(communityId.code, 50, 0) }.items.map { it.mapToCommunityLeaderDomain() }

    override suspend fun getCommunitiesInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<CommunityDomain> =
        callProxy.call { commun4j.getBlacklistedCommunities(CyberName(userId.userId)) }.items.map { it.mapToCommunityDomain() }

    override suspend fun moveCommunityToBlackList(communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.hide(communCode = CyberSymbolCode(communityId.code), user = CyberName(currentUserRepository.userId.userId), bandWidthRequest = BandWidthRequest.bandWidthFromComn, clientAuthRequest = ClientAuthRequest.empty, key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun moveCommunityFromBlackList(communityId: CommunityIdDomain) {
        callProxy.callBC {
            commun4j.unHide(communCode = CyberSymbolCode(communityId.code), user = CyberName(currentUserRepository.userId.userId), bandWidthRequest = BandWidthRequest.bandWidthFromComn, clientAuthRequest = ClientAuthRequest.empty, key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun voteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain) {
        callProxy.callBC {
            commun4j.voteLeader(communCode = CyberSymbolCode(communityId.code), leader = CyberName(leader.userId), pct = null, bandWidthRequest = BandWidthRequest.bandWidthFromComn, voter = CyberName(currentUserRepository.userId.userId), key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun unvoteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain) {
        callProxy.callBC {
            commun4j.unVoteLeader(communCode = CyberSymbolCode(communityId.code), leader = CyberName(leader.userId), bandWidthRequest = BandWidthRequest.bandWidthFromComn, voter = CyberName(currentUserRepository.userId.userId), key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun getSubscribers(communityId: CommunityIdDomain, offset: Int, pageSizeLimit: Int): List<UserDomain> {
        return callProxy.call {
            commun4j.getSubscribers(null, communityId.code, pageSizeLimit, offset)
        }.items.map { it.mapToUserDomain() }
    }

    override suspend fun getUserCommunities(userIdDomain: UserIdDomain, offset: Int, pageSizeLimit: Int): List<CommunityDomain> =
        callProxy.call { commun4j.getCommunitySubscriptions(CyberName(userIdDomain.userId), pageSizeLimit, offset) }.map { it.mapToCommunityDomain() }

    override suspend fun getCommunityReports(communityId: CommunityIdDomain, type: ReportRequestContentType, status: ReportsRequestStatus, sortType: ReportsRequestTimeSort, limit: Int, offset: Int): List<ReportedPostDomain> {
        val call= callProxy.call {
            commun4j.getReports(
                communityIds = listOf(communityId.code),
                status = status,
                contentType = type,
                sortBy = sortType,
                limit = limit,
                offset = offset)
        }.items.map {
            it.mapToReportedPostDomain(currentUserRepository.userId.userId)
        }

        return call
    }

    override suspend fun getEntityReports(communityId: CommunityIdDomain, userIdDomain: UserIdDomain, permlink: String, limit: Int, offset: Int): List<EntityReportDomain> {
        return callProxy.call {
            commun4j.getEntityReports(communityId.code, CyberName(userIdDomain.userId), permlink, limit, offset)
        }.items.map {
            it.mapTReportDomain()
        }

    }

    override suspend fun getProposals(communityId: CommunityIdDomain, limit: Int, offset: Int):List<ProposalDomain> {
        val response = callProxy.call {
            commun4j.getProposals(communityIds = listOf(communityId.code), limit = limit, offset = offset)
        }.items.map {
            it.mapToProposalDomain()
        }
        return response
    }

}