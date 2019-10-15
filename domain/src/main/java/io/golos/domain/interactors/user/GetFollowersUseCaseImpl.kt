package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowersPageDomain
import javax.inject.Inject

class GetFollowersUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository) : GetFollowersUseCase {

    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain {
        return usersRepository.getFollowers(query, sequenceKey, pageSizeLimit)
    }
}