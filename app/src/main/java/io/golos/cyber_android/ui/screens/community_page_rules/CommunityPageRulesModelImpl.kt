package io.golos.cyber_android.ui.screens.community_page_rules

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class CommunityPageRulesModelImpl @Inject constructor(private val rules: String?): ModelBaseImpl(), CommunityPageRulesModel {

    override fun getRules(): String? = rules
}