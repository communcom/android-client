package io.golos.cyber_android.ui.screens.community_page_rules

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class CommunityPageRulesViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageRulesModel
) : ViewModelBase<CommunityPageRulesModel>(dispatchersProvider, model) {

    private val communityPageRulesMutableLiveData = MutableLiveData<String>()

    val communityPageRulesLiveData = communityPageRulesMutableLiveData.toLiveData()

    fun start(){
        communityPageRulesMutableLiveData.value = model.getRules()
    }
}