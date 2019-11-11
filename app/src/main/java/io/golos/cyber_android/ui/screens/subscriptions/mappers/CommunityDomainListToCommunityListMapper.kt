package io.golos.cyber_android.ui.screens.subscriptions.mappers

import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.domain.dto.CommunityDomain

class CommunityDomainListToCommunityListMapper: Function1<List<CommunityDomain>, List<Community>> {

    override fun invoke(communitiesDomainList: List<CommunityDomain>): List<Community> {
        val communitiesList = mutableListOf<Community>()
        communitiesDomainList.forEach {
            communitiesList.add(CommunityDomainToCommunityMapper().invoke(it))
        }
        return communitiesList
    }
}