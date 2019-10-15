package io.golos.cyber_android.ui.screens.followers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: FollowersModel,
    private val paginator: Paginator.Store<Community>
) : ViewModelBase<FollowersModel>(dispatchersProvider, model) {

    private var query: String? = null

    private val _followersListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val followersListStateLiveData = _followersListStateLiveData as LiveData<Paginator.State>


    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadFollowers(it.sequenceKey)
            }
        }
        paginator.render = {
            _followersListStateLiveData.value = it
        }
    }

    private fun loadFollowers(sequenceKey: String?){
        launch {
            model.getFollowers(query, sequenceKey, PAGE_SIZE_LIMIT)
        }
    }

    fun start(){

    }

    fun back() {

    }

    private companion object {

        private const val PAGE_SIZE_LIMIT = 30
    }
}