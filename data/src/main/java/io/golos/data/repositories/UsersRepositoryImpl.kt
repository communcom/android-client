package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.user.UsersApi
import io.golos.data.mappers.mapToUserProfileDomain
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.FollowerDomain
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.user.UsersRepository
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.random.Random

class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider),
    UsersRepository {

    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        return withContext(dispatchersProvider.ioDispatcher){
            usersApi.getFollowers(query, offset, pageSizeLimit)
        }
    }

    override suspend fun subscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            usersApi.subscribeToFollower(userId)
        }
    }

    override suspend fun unsubscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            usersApi.unsubscribeToFollower(userId)
        }
    }

    override suspend fun getUserProfile(user: CyberName): UserProfileDomain =
        apiCall { commun4j.getUserProfile(user, null) }.mapToUserProfileDomain()
}