package io.golos.domain.interactors.community

/**
 * Check subscriptions on communities
 */
interface CheckSubscriptionsOnCommunitiesUseCase {

    suspend fun isContainSubscriptionsOnCommunities(): Boolean
}