package io.golos.domain.use_cases.user

import io.golos.domain.dto.FollowerDomain
import javax.inject.Inject

class GetFollowersUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository) : GetFollowersUseCase {

    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        return usersRepository.getFollowers(query, offset, pageSizeLimit)
    }
}