package io.golos.cyber_android.ui.screens.followers

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers.FollowersFragmentComponent
import io.golos.cyber_android.databinding.FragmentFollowersBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.item_toolbar.*
import kotlinx.android.synthetic.main.view_search_bar.*

class FollowersFragment : FragmentBaseMVVM<FragmentFollowersBinding, FollowersModel, FollowersViewModel>() {

    private val adapter by lazy { FollowersAdapter() }

    override fun provideViewModelType(): Class<FollowersViewModel> = FollowersViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_followers

    override fun inject() = App.injections
        .get<FollowersFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentFollowersBinding, viewModel: FollowersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFollowersList()
        observeViewModel()
        btnRetry.setOnClickListener {
            viewModel.start()
        }
        viewModel.start()
    }

    private fun observeViewModel() {
        viewModel.followersListStateLiveData.observe(this, Observer {
            when (it) {
                is Paginator.State.Data<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isFullData = false
                    adapter.isPageError = false
                    adapter.isSearchProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isFullData = true
                    adapter.isPageError = false
                    adapter.isSearchProgress = false
                    generalProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isPageError = true
                }
                is Paginator.State.NewPageProgress<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isPageError = false
                }
                is Paginator.State.SearchProgress<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isSearchProgress = true
                }
                is Paginator.State.SearchPageError<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isSearchProgress = false
                    uiHelper.showMessage(R.string.loading_error)
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

        viewModel.searchErrorVisibilityLiveData.observe(this, Observer {
            if (it) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupFollowersList() {
        rvFollowers.layoutManager = LinearLayoutManager(requireContext())
        rvFollowers.adapter = adapter
        adapter.nextPageCallback = {
            viewModel.loadMoreFollowers()
        }
        adapter.onPageRetryLoadingCallback = {
            viewModel.loadMoreFollowers()
        }
        adapter.onJoinClickedCallback = {
            viewModel.changeFollowingStatus(it)
        }
    }

    private fun setupToolbar() {
        toolbarTitle.setText(R.string.followers)
        ivBack.setOnClickListener {
            viewModel.back()
        }
    }
}