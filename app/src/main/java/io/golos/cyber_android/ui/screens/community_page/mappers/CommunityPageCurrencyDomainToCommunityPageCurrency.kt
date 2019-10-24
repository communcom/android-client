package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.CommunityPage
import io.golos.domain.entities.CommunityPageDomain

class CommunityPageCurrencyDomainToCommunityPageCurrency :
    Function1<CommunityPageDomain.CommunityPageCurrencyDomain, CommunityPage.CommunityPageCurrency> {

    override fun invoke(communityPageCurrencyDomain: CommunityPageDomain.CommunityPageCurrencyDomain): CommunityPage.CommunityPageCurrency {
        return CommunityPage.CommunityPageCurrency(
            communityPageCurrencyDomain.currencyName,
            communityPageCurrencyDomain.exchangeRate
        )
    }


}
