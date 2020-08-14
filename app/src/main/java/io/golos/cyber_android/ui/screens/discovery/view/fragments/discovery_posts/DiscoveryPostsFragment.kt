package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts

import io.golos.cyber_android.databinding.FragmentMyFeedBinding
import io.golos.cyber_android.ui.screens.feed_my.view.MyFeedFragment
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedViewModel

class DiscoveryPostsFragment : MyFeedFragment() {

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        viewModel.isUserCreatePostVisible = false
        super.linkViewModel(binding, viewModel)
    }
}