package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowersDomain
import javax.inject.Inject

class GetFollowersUseCaseImpl @Inject constructor(private val userRepository: UserRepository): GetFollowersUseCase {

    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): List<FollowersDomain> {
        return userRepository.getFollowers(query, sequenceKey, pageSizeLimit)
    }
}