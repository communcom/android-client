package io.golos.cyber_android.ui.screens.community_page_rules

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CommunityRuleDomain
import javax.inject.Inject

class CommunityPageRulesModelImpl @Inject constructor(
    private val rules: List<CommunityRuleDomain>
): ModelBaseImpl(), CommunityPageRulesModel {

    override fun getRules() = rules
}