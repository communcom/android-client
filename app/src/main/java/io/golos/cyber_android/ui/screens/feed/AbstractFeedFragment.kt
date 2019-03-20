package io.golos.cyber_android.ui.screens.feed

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.CommunityFeedViewModel
import io.golos.cyber_android.serviceLocator
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest

abstract class AbstractFeedFragment<T : PostFeedUpdateRequest> : Fragment() {

    abstract fun provideViewModel(): AbstractFeedViewModel<T>

}

class ExampleFrag : AbstractFeedFragment<CommunityFeedUpdateRequest>() {
    override fun provideViewModel(): AbstractFeedViewModel<CommunityFeedUpdateRequest> {
        return ViewModelProviders.of(
            this,
            requireActivity().serviceLocator.getCommunityFeedViewModelFactory(CommunityId("gls"))
        ).get(CommunityFeedViewModel::class.java)
    }

}