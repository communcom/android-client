package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowerDomain
import javax.inject.Inject

class GetFollowersUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository) : GetFollowersUseCase {

    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        return usersRepository.getFollowers(query, offset, pageSizeLimit)
    }
}