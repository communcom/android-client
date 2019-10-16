package io.golos.data.api.communities

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.commun4j.Commun4j
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.entities.CommunityDomain
import io.golos.domain.utils.MurmurHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
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
    private val dispatchersProvider: DispatchersProvider
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
                Timber.e(ex)
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
                Timber.e(ex)
                throw ex
            }
        }

    override suspend fun getCommunityById(communityId: CommunityId): Community? =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            communities.firstOrNull { it.id == communityId }
        }

    override suspend fun unsubscribeToCommunity(communityId: String) {
        delay(2000)
        randomException()
    }

    override suspend fun subscribeToCommunity(communityId: String) {
        delay(2000)
        randomException()
    }

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        delay(2000)
        if(offset == 0){
            val rand = Random
            return if(rand.nextBoolean()){
                getMockCommunitiesList()
            } else{
                emptyList()
            }
        }
        randomException()
        return getMockCommunitiesList()
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

    private fun randomException(){
        val rand = Random
        if(rand.nextBoolean()){
            throw RuntimeException()
        }
    }

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        delay(2000)
        randomException()
        return getMockCommunitiesList()
    }

    private fun getMockCommunitiesList(): List<CommunityDomain> {
        val communityNamesArray = mutableListOf<String>()
        communityNamesArray.add("Overwatch")
        communityNamesArray.add("Commun")
        communityNamesArray.add("ADME")
        communityNamesArray.add("Dribbble")
        communityNamesArray.add("Behance")

        val communityLogoArray = mutableListOf<String>()
        communityLogoArray.add("https://images.fastcompany.net/image/upload/w_596,c_limit,q_auto:best,f_auto/fc/3034007-inline-i-applelogo.jpg")
        communityLogoArray.add("https://brandmark.io/logo-rank/random/beats.png")
        communityLogoArray.add("https://brandmark.io/logo-rank/random/pepsi.png")
        communityLogoArray.add("https://99designs-start-attachments.imgix.net/alchemy-pictures/2019%2F01%2F31%2F23%2F04%2F58%2Ff99d01d7-bf50-4b79-942f-605d6ed1fdce%2Fludibes.png?auto=format&ch=Width%2CDPR&w=250&h=250")

        val communityList =  mutableListOf<CommunityDomain>()
        val rand = Random

        for(i in 0..30){
            val communityName = communityNamesArray[rand.nextInt(communityNamesArray.size - 1)]
            val communityLogo: String = communityLogoArray[rand.nextInt(communityLogoArray.size - 1)]
            val communityDomain = CommunityDomain(UUID.randomUUID().toString(), communityName, communityLogo, rand.nextInt(1000000).toLong(), rand.nextBoolean())
            communityList.add(communityDomain)
        }
        return communityList
    }
}