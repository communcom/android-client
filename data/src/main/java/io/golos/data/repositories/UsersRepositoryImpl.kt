package io.golos.data.repositories

import io.golos.data.api.user.UsersApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.FollowerDomain
import io.golos.domain.interactors.user.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val dispatchersProvider: DispatchersProvider
) : UsersRepository {

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
}