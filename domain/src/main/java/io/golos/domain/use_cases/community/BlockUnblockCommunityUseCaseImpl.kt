package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain
import javax.inject.Inject

class BlockUnblockCommunityUseCaseImpl
@Inject
constructor(private val communitiesRepository: CommunitiesRepository):BlockUnblockCommunityUseCase {
    override suspend fun blockCommunity(communityIdDomain: CommunityIdDomain) = communitiesRepository.moveCommunityToBlackList(communityIdDomain)

    override suspend fun unBlockCommunity(communityIdDomain: CommunityIdDomain) = communitiesRepository.moveCommunityFromBlackList(communityIdDomain)
}