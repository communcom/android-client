package io.golos.data.repositories

import io.golos.data.api.user.UsersApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.FollowersPageDomain
import io.golos.domain.interactors.user.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val dispatchersProvider: DispatchersProvider
) : UsersRepository {

    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain {
        return withContext(dispatchersProvider.ioDispatcher){
            usersApi.getFollowers(query, sequenceKey, pageSizeLimit)
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
}