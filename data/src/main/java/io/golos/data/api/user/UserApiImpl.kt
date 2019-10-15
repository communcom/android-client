package io.golos.data.api.user

import io.golos.domain.entities.FollowersPageDomain
import java.util.*
import javax.inject.Inject

class UserApiImpl @Inject constructor() : UserApi {


    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain {
        return FollowersPageDomain(UUID.randomUUID().toString(), emptyList())
    }

    override suspend fun subscribeToFollower(userId: String) {

    }

    override suspend fun unsubscribeToFollower(userId: String) {

    }
}