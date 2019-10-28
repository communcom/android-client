package io.golos.cyber_android.ui.screens.community_page_rules

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase

interface CommunityPageRulesModel : ModelBase{

    fun getRules(): String?
}