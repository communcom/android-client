package io.golos.cyber_android.ui.screens.community_page_about

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class CommunityPageAboutViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageAboutModel
) : ViewModelBase<CommunityPageAboutModel>(dispatchersProvider, model) {

    private val communityPageDescriptionMutableLiveData = MutableLiveData<String>()

    val communityPageDescriptionLiveData = communityPageDescriptionMutableLiveData.toLiveData()

    fun start(){
        communityPageDescriptionMutableLiveData.value = model.getDescription()
    }
}