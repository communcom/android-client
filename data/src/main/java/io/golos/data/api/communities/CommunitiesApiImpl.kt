package io.golos.data.api.communities

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.commun4j.Commun4j
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.CommunityId
import io.golos.shared_core.MurmurHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class CommunitiesApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead,
    private val appResources: AppResourcesProvider,
    private val moshi: Moshi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : Commun4jApiBase(commun4j, currentUserRepository), CommunitiesApi {

    private val communities: List<Community> by lazy { loadCommunities() }

    private data class CommunityRaw (
        val id: String,
        val name: String,
        val followersQuantity: Int,
        val logoUrl: String
    )

    override suspend fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): List<Community> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            delay(500)

            try {
                communities
                    .asSequence()
                    .filter {
                        if(isUser) {
                            isUserCommunity(it)
                        } else {
                            !isUserCommunity(it)
                        }
                    }
                    .drop(offset)
                    .take(pageSize)
                    .toList()

            } catch(ex: Exception) {
                logger.log(ex)
                throw ex
            }
        }

    override suspend fun joinToCommunity(externalId: String) =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
        }

    override suspend fun searchInCommunities(query: String, isUser: Boolean): List<Community> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            delay(500)

            val queryLower = query.toLowerCase()

            try {
                communities
                    .asSequence()
                    .filter {
                        if(isUser) {
                            isUserCommunity(it)
                        } else {
                            !isUserCommunity(it)
                        }
                    }
                    .filter { it.name.toLowerCase().contains(queryLower) }
                    .toList()
            } catch(ex: Exception) {
                logger.log(ex)
                throw ex
            }
        }

    override suspend fun getCommunityById(communityId: CommunityId): Community? =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            communities.firstOrNull { it.id == communityId }
        }

    private fun loadCommunities(): List<Community> {
        val random = Random(Date().time)

        return String(appResources.getCommunities().readBytes())
            .let {
                moshi.adapter<List<CommunityRaw>>(
                    Types.newParameterizedType(
                        List::class.java,
                        CommunityRaw::class.java
                    )
                ).fromJson(it)!!
            }
            .sortedBy {
                it.name
            }
            .map {
                val followersQuantity = when {
                    it.followersQuantity < 10 -> it.followersQuantity * random.nextInt(5)
                    it.followersQuantity < 100 -> it.followersQuantity * random.nextInt(50)
                    else -> it.followersQuantity * random.nextInt(500)
                }
                Community(CommunityId(it.id), it.name, followersQuantity, it.logoUrl)
            }
    }

    private fun isUserCommunity(communityExt: Community) = MurmurHash.hash64(communityExt.name) % 2 == 0L
}