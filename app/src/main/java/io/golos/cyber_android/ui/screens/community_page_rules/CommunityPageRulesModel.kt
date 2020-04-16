package io.golos.cyber_android.ui.screens.community_page_rules

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityRuleDomain

interface CommunityPageRulesModel : ModelBase{
    fun getRules(): List<CommunityRuleDomain>
}