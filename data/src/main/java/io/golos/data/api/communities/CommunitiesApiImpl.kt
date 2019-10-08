package io.golos.data.api.communities

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.GetCommunitiesItem
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.commun_entities.Community
import javax.inject.Inject

class CommunitiesApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), CommunitiesApi {
    override fun getCommunitiesList(offset: Int, pageSize: Int): List<Community>  =
        commun4j
            .getCommunitiesList(authState.user, offset, pageSize)
            .getOrThrow()
            .items
            .map { it.map() }

    private fun GetCommunitiesItem.map(): Community = Community(communityId, avatarUrl, name, subscribersCount)
}