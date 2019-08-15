package io.golos.cyber_android.ui.screens.main_activity.communities.data_repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityExt
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.sharedmodel.Either
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class CommunitiesRepositoryImpl
@Inject
constructor(
    private val appResources: AppResourcesProvider,
    private val moshi: Moshi,
    private val dispatchersProvider: DispatchersProvider
) : CommunitiesRepository {
    private val communities: List<CommunityExt> by lazy { loadCommunities() }

    override suspend fun getCommunities(top: Int, skip: Int, type: CommunityType): Either<List<CommunityExt>, Throwable> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            delay(500)

            try {
                communities
                    .asSequence()
                    .filter {
                        when(type) {
                            CommunityType.USER -> isUserCommunity(it)
                            CommunityType.DISCOVERED -> !isUserCommunity(it)
                        }
                    }
                    .drop(skip)
                    .take(top)
                    .toList()
                    .let { Either.Success<List<CommunityExt>, Throwable>(it) }
            } catch(ex: Exception) {
                Either.Failure<List<CommunityExt>, Throwable>(ex)
            }
        }

    override suspend fun joinToCommunity(externalId: String): Either<Unit, Throwable> =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            Either.Success<Unit, Throwable>(Unit)
        }

    override suspend fun searchInCommunities(query: String, type: CommunityType): Either<List<CommunityExt>, Throwable> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            delay(500)

            val queryLower = query.toLowerCase()

            communities
                .asSequence()
                .filter {
                    when(type) {
                        CommunityType.USER -> isUserCommunity(it)
                        CommunityType.DISCOVERED -> !isUserCommunity(it)
                    }
                }
                .filter { it.name.toLowerCase().contains(queryLower) }
                .toList()
                .let { Either.Success<List<CommunityExt>, Throwable>(it) }
        }

    private fun loadCommunities(): List<CommunityExt> {
        val random = Random(Date().time)

        return String(appResources.getCommunities().readBytes())
            .let {
                moshi.adapter<List<CommunityExt>>(
                    Types.newParameterizedType(
                        List::class.java,
                        CommunityExt::class.java
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
                it.copy(followersQuantity = followersQuantity)
            }
    }

    private fun isUserCommunity(communityExt: CommunityExt) = communityExt.followersQuantity % 2 == 0
}