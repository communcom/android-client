package io.golos.cyber_android.ui.screens.my_feed.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.my_feed.MyFeedFragmentComponent
import io.golos.cyber_android.databinding.FragmentMyFeedBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.GetPostsConfiguration
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.cyber_android.ui.screens.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.posts_list.view_model.PostsListViewModel
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.fragment_my_feed.btnRetry
import kotlinx.android.synthetic.main.fragment_my_feed.generalProgressLoading
import kotlinx.android.synthetic.main.view_search_bar.*

class MyFeedFragment : FragmentBaseMVVM<FragmentMyFeedBinding, PostsListViewModel>() {

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: PostsListViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<PostsListViewModel> = PostsListViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_my_feed

    override fun inject() = App.injections.get<MyFeedFragmentComponent>(arguments!!.getParcelable(UPLOAD_POSTS_CONFIGURATION))
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<MyFeedFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPosts.adapter = MyFeedAdapter()
        observeViewModel()
        viewModel.start()
    }

    private fun observeViewModel(){
        viewModel.postsListState.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            when (it) {
                is Paginator.State.Data<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isFullData = false
                    myFeedAdapter.isPageError = false
                    myFeedAdapter.isSearchProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isFullData = true
                    myFeedAdapter.isPageError = false
                    myFeedAdapter.isSearchProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isPageError = true
                }
                is Paginator.State.NewPageProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isPageError = false
                }
                is Paginator.State.SearchProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isSearchProgress = true
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isSearchProgress = false
                    uiHelper.showMessage(R.string.loading_error)
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyProgress -> {
                    generalProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.Empty -> {
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    generalProgressLoading.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

    companion object {

        private const val UPLOAD_POSTS_CONFIGURATION = "UPLOAD_POSTS_CONFIGURATION"

        fun newInstance(getPostsConfiguration: GetPostsConfiguration): Fragment {
            val postsListFragment = MyFeedFragment()
            postsListFragment.apply {
                arguments = bundleOf(
                    UPLOAD_POSTS_CONFIGURATION to getPostsConfiguration
                )
            }
            return postsListFragment
        }
    }
}