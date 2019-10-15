package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowersPageDomain
import javax.inject.Inject

class GetFollowersUseCaseImpl @Inject constructor(private val userRepository: UserRepository) : GetFollowersUseCase {

    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain {
        return userRepository.getFollowers(query, sequenceKey, pageSizeLimit)
    }
}