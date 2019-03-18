package io.golos.cyber_android

import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.CommunityFeedUseCase

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedViewModel(private val communityFeedUserCase: CommunityFeedUseCase) : ViewModel() {
    init {
        communityFeedUserCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        communityFeedUserCase.unsubscribe()
    }
}