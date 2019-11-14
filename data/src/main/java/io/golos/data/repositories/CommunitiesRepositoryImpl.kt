package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.getOrThrow
import io.golos.data.mappers.GetCommunitiesItemToCommunityDomainMapper
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityPageDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class CommunitiesRepositoryImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider,

    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepositoryRead
) : CommunitiesRepository {

    override suspend fun getCommunityPageById(communityId: String): CommunityPageDomain {
        return withContext(dispatchersProvider.ioDispatcher){
            communitiesApi.getCommunityPageById(communityId)
        }
    }

    override suspend fun subscribeToCommunity(communityId: String) {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.subscribeToCommunity(communityId)
        }
    }

    override suspend fun unsubscribeToCommunity(communityId: String) {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.unsubscribeToCommunity(communityId)
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

    /**
     * [forCurrentUserOnly] if true the method returns only current users' communities (otherwise - all communities)
     */
    override fun getCommunitiesList(offset: Int, pageSize: Int, forCurrentUserOnly: Boolean): List<CommunityDomain> {
        try {
            if(forCurrentUserOnly) {
                throw UnsupportedOperationException("Getting communities for current user is not supported now")
            }

            return commun4j
                .getCommunitiesList(currentUserRepository.user, offset, pageSize)
                .getOrThrow()
                .items
                .map { GetCommunitiesItemToCommunityDomainMapper.invoke(it) }
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }
    }
}

/**
 * Architecture notes
 * 1. We must get rid of Api layer and should use Commun4j directly in repositories
 * 2. No Kotlin coroutines in repositories (in general case) - it's a place for data extraction logic only
 * 3. Repositories must use *Domain entities, simple types and enums for input/output only
 * */
