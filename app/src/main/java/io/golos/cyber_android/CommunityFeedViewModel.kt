package io.golos.cyber_android

import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.model.UpdateOption

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedViewModel(private val communityFeedUserCase: CommunityFeedUseCase) : ViewModel() {
    init {
        communityFeedUserCase.subscribe()
        communityFeedUserCase.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    override fun onCleared() {
        super.onCleared()
        communityFeedUserCase.unsubscribe()
    }
}