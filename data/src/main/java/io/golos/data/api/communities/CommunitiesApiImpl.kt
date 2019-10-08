package io.golos.data.api.communities

import io.golos.commun4j.Commun4j
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import javax.inject.Inject

class CommunitiesApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), CommunitiesApi {

    // It'll be getCommunities method in a future
    override fun getCommunitiesList(): List<Unit> = listOf()
}