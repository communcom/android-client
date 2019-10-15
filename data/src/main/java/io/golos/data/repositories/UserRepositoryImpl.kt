package io.golos.data.repositories

import io.golos.data.api.user.UserApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.FollowersDomain
import io.golos.domain.interactors.user.UserRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val dispatchersProvider: DispatchersProvider
) : UserRepository {

    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): List<FollowersDomain> {
        return withContext(dispatchersProvider.ioDispatcher){
            userApi.getFollowers(query, sequenceKey, pageSizeLimit)
        }
    }

    override suspend fun subscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            userApi.subscribeToFollower(userId)
        }
    }

    override suspend fun unsubscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            userApi.unsubscribeToFollower(userId)
        }
    }
}