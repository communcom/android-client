package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import io.golos.cyber_android.databinding.FragmentMyFeedBinding
import io.golos.cyber_android.ui.screens.discovery.view.DiscoveryFragmentTab
import io.golos.cyber_android.ui.screens.feed_my.view.MyFeedFragment
import io.golos.cyber_android.ui.screens.feed_my.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedViewModel
import kotlinx.android.synthetic.main.fragment_my_feed.*

open class DiscoveryPostsFragment : MyFeedFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val pFragment = parentFragment
        if(pFragment is DiscoveryFragmentTab){
            pFragment.getPostsLiveData().observe(viewLifecycleOwner, Observer {
                it?.let {
                    (rvPosts.adapter as MyFeedAdapter).updateMyFeedPosts(it)
                }
            })
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        viewModel.isUserCreatePostVisible = false
        viewModel.updatePaddingVisibility(View.GONE)
        super.linkViewModel(binding, viewModel)
    }
}