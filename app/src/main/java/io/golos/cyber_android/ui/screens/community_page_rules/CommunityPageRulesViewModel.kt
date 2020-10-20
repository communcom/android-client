package io.golos.cyber_android.ui.screens.community_page_rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityRuleDomain
import javax.inject.Inject

class CommunityPageRulesViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageRulesModel
) : ViewModelBase<CommunityPageRulesModel>(dispatchersProvider, model) {

    private val communityPageRulesMutableLiveData = MutableLiveData<List<CommunityRuleDomain>>()
    val communityPageRulesLiveData: LiveData<List<CommunityRuleDomain>> get() = communityPageRulesMutableLiveData

    fun start(){
        communityPageRulesMutableLiveData.value = model.getRules()
    }
}