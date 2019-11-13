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
import io.golos.cyber_android.ui.common.utils.TopDividerItemDecoration
import io.golos.cyber_android.ui.dto.GetPostsConfiguration
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedViewModel
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*
import timber.log.Timber

class MyFeedFragment : FragmentBaseMVVM<FragmentMyFeedBinding, MyFeedViewModel>() {

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<MyFeedViewModel> = MyFeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_my_feed

    override fun inject() = App.injections.get<MyFeedFragmentComponent>(arguments!!.getParcelable(UPLOAD_POSTS_CONFIGURATION))
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<MyFeedFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostsList()
        observeViewModel()
        viewModel.start()
    }

    private fun setupPostsList() {
        val myFeedAdapter = MyFeedAdapter()
        rvPosts.addItemDecoration(TopDividerItemDecoration(requireContext()))
        rvPosts.adapter = myFeedAdapter

        myFeedAdapter.nextPageCallback = {
            Timber.d("paginator: load more posts")
            viewModel.loadMorePosts()
        }
        myFeedAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMorePosts()
        }
    }

    private fun observeViewModel() {
        viewModel.postsListState.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            Timber.d("paginator:[${it::class.java.simpleName.toUpperCase()}]")
            when (it) {
                is Paginator.State.Data<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isFullData = false
                    myFeedAdapter.isPageError = false
                    myFeedAdapter.isNewPageProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
//                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isFullData = true
                    myFeedAdapter.isPageError = false
                    myFeedAdapter.isNewPageProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isPageError = true
                }
                is Paginator.State.NewPageProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isPageError = false
                    myFeedAdapter.isNewPageProgress = true
                }
                is Paginator.State.SearchProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isNewPageProgress = true
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.isNewPageProgress = false
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
        viewModel.user.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            myFeedAdapter.updateUser(it)
        })
        viewModel.loadUserProgressVisibility.observe(this, Observer {
            if(it){
                generalProgressLoading.visibility = View.VISIBLE
            } else{
                generalProgressLoading.visibility = View.INVISIBLE
            }
        })
        viewModel.loadUserErrorVisibility.observe(this, Observer {
            if(it){
                btnRetry.visibility = View.VISIBLE
            } else{
                btnRetry.visibility = View.INVISIBLE
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