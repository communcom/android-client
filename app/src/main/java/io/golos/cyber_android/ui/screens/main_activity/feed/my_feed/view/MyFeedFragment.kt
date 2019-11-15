package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.di.MyFeedFragmentComponent
import io.golos.cyber_android.databinding.FragmentMyFeedBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.utils.DividerPostDecoration
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.list.MyFeedAdapter
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model.MyFeedViewModel
import kotlinx.android.synthetic.main.fragment_my_feed.*
import kotlinx.android.synthetic.main.view_search_bar.*

class MyFeedFragment : FragmentBaseMVVM<FragmentMyFeedBinding, MyFeedViewModel>() {

    override fun linkViewModel(binding: FragmentMyFeedBinding, viewModel: MyFeedViewModel) {
        binding.viewModel = viewModel
    }

    override fun provideViewModelType(): Class<MyFeedViewModel> = MyFeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_my_feed

    override fun inject() = App.injections.get<MyFeedFragmentComponent>()
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
        val lManager = LinearLayoutManager(context)

        rvPosts.addItemDecoration(DividerPostDecoration(requireContext()))
        rvPosts.layoutManager = lManager

        rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = lManager.itemCount
                val firstVisibleItem = lManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleItemCount) {
                    if (lManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1) {
                        viewModel.loadMorePosts()
                    }
                }
            }
        })

        rvPosts.adapter = myFeedAdapter
        myFeedAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMorePosts()
        }
    }

    private fun observeViewModel() {
        viewModel.postsListState.observe(viewLifecycleOwner, Observer {
            val myFeedAdapter = rvPosts.adapter as MyFeedAdapter
            when (it) {
                is Paginator.State.Data<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.hideLoadingNextPageProgress()
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.hideLoadingNextPageProgress()
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    myFeedAdapter.hideLoadingNextPageProgress()
                    myFeedAdapter.showLoadingNextPageError()
                    rvPosts.scrollToPosition(myFeedAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    myFeedAdapter.hideLoadingNextPageError()
                    myFeedAdapter.showLoadingNextPageProgress()
                    rvPosts.scrollToPosition(myFeedAdapter.itemCount - 1)
                }
                is Paginator.State.SearchProgress<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.showLoadingNextPageProgress()
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    myFeedAdapter.updateMyFeedPosts(it.data as MutableList<Post>)
                    myFeedAdapter.hideLoadingNextPageProgress()
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
            if (it) {
                generalProgressLoading.visibility = View.VISIBLE
            } else {
                generalProgressLoading.visibility = View.INVISIBLE
            }
        })
        viewModel.loadUserErrorVisibility.observe(this, Observer {
            if (it) {
                btnRetry.visibility = View.VISIBLE
            } else {
                btnRetry.visibility = View.INVISIBLE
            }
        })
    }

    override fun onDestroyView() {
        rvPosts.adapter = null
        super.onDestroyView()
    }

    companion object {

        fun newInstance(): Fragment {

            return MyFeedFragment()
        }
    }
}