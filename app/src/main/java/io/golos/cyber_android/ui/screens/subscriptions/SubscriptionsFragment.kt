package io.golos.cyber_android.ui.screens.subscriptions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions.SubscriptionsFragmentComponent
import io.golos.cyber_android.databinding.FragmentSubscriptionsBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.paginator.Paginator
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToSearchCommunitiesCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.utils.debounce
import io.golos.cyber_android.utils.throttleLatest
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.android.synthetic.main.item_toolbar.*
import kotlinx.android.synthetic.main.view_search_bar.*
import timber.log.Timber

class SubscriptionsFragment : FragmentBaseMVVM<FragmentSubscriptionsBinding, SubscriptionsModel, SubscriptionsViewModel>() {

    private val subscriptionsAdapter: CommunitiesAdapter = CommunitiesAdapter()

    override fun provideViewModelType(): Class<SubscriptionsViewModel> = SubscriptionsViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_subscriptions

    override fun inject() = App.injections
        .get<SubscriptionsFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentSubscriptionsBinding, viewModel: SubscriptionsViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupSearch()
        setupCommunitiesRecommendedList()
        setupSubscriptionsList()
        setupToolbar()
        viewModel.start()
    }

    private fun setupToolbar() {
        toolbarTitle.setText(R.string.subscriptions)
        ivBack.setOnClickListener {
            viewModel.back()
        }
    }

    private fun setupCommunitiesRecommendedList() {
        rvCommunitiesRecommended.layoutManager = LinearLayoutManager(requireContext())
        rvCommunitiesRecommended.adapter = subscriptionsAdapter
        subscriptionsAdapter.nextPageCallback = {
            viewModel.loadMoreRecommendedCommunities()
        }
        subscriptionsAdapter.onPageRetryLoadingCallback = {
            viewModel.loadMoreRecommendedCommunities()
        }
        subscriptionsAdapter.onJoinClickedCallback = {
            viewModel.changeCommunitySubscriptionStatus(it)
        }
    }

    private fun setupSubscriptionsList() {
        rvSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        rvSubscriptions.adapter = subscriptionsAdapter
        subscriptionsAdapter.nextPageCallback = {
            viewModel.loadSubscriptions()
        }
        subscriptionsAdapter.onPageRetryLoadingCallback = {
            viewModel.loadSubscriptions()
        }
        subscriptionsAdapter.onJoinClickedCallback = {
            viewModel.changeCommunitySubscriptionStatus(it)
        }
    }

    private fun setupSearch() {


        searchBar.addTextChangedListener(object : TextWatcher {

            private val querySearchListener: (String) -> Unit = debounce({
                viewModel.onCommunitySearchQueryChanged(it)
            })

            override fun afterTextChanged(s: Editable?) {
                //implement if need
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //implement if need
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                querySearchListener(s.toString())
            }

        })
    }

    private fun observeViewModel() {
        viewModel.subscriptionsStateLiveData.observe(this, Observer {
            when (it) {
                SubscriptionsViewModel.SubscriptionsState.UNDEFINED -> setUndefinedSubscriptionsState()
                SubscriptionsViewModel.SubscriptionsState.EMPTY -> setEmptySubscriptionsState()
                SubscriptionsViewModel.SubscriptionsState.EXIST -> setExistSubscriptionsState()
                else -> {
                    Timber.e("undefined subscription state type")
                }
            }
        })
        viewModel.command.observe(this, Observer {
            when (it) {
                is NavigateToSearchCommunitiesCommand -> {

                }
                is BackCommand -> {

                }
                is ShowMessageCommand -> {
                    uiHelper.showMessage(it.textResId)
                }
            }
        })
        viewModel.subscriptionsListStateLiveData.observe(this, Observer {
            updateListState(it)
        })
        viewModel.recommendedSubscriptionsListStateLiveData.observe(this, Observer {
            updateListState(it)
        })

        viewModel.generalLoadingProgressVisibilityLiveData.observe(this, Observer {
            if (it) {
                generalProgressLoading.visibility = View.VISIBLE
            } else {
                generalProgressLoading.visibility = View.INVISIBLE
            }
        })

        viewModel.recommendedSubscriptionStatusLiveData.observe(this, Observer {
            subscriptionsAdapter.updateSubscriptionStatus(it)
        })
        viewModel.subscriptionsStatusLiveData.observe(this, Observer {
            subscriptionsAdapter.updateSubscriptionStatus(it)
        })
        viewModel.generalErrorVisibilityLiveData.observe(this, Observer{
            if(it){
                btnRetry.visibility = View.VISIBLE
            } else{
                btnRetry.visibility = View.INVISIBLE
            }
        })
        viewModel.searchErrorVisibilityLiveData.observe(this, Observer {
            if(it){
                pbLoading.visibility = View.VISIBLE
            } else{
                pbLoading.visibility = View.INVISIBLE
            }
        })
    }

    private fun updateListState(state: Paginator.State) {
        when (state) {
            is Paginator.State.Data<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = false
                subscriptionsAdapter.isPageError = false
                subscriptionsAdapter.isSearchProgress = false
            }
            is Paginator.State.FullData<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = true
                subscriptionsAdapter.isPageError = false
                subscriptionsAdapter.isSearchProgress = false
            }
            is Paginator.State.PageError<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isPageError = true
            }
            is Paginator.State.NewPageProgress<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isPageError = false
            }
            is Paginator.State.SearchProgress<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isSearchProgress = true
            }
            is Paginator.State.SearchPageError<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isSearchProgress = false
                uiHelper.showMessage(R.string.loading_error)
            }
        }
    }

    private fun setUndefinedSubscriptionsState() {
        layoutSearchBar.visibility = View.INVISIBLE
        rvSubscriptions.visibility = View.INVISIBLE
        clNoSubscriptionsPlaceHolder.visibility = View.INVISIBLE
        tvRecommendedTitle.visibility = View.INVISIBLE
        rvCommunitiesRecommended.visibility = View.INVISIBLE
    }

    private fun setEmptySubscriptionsState() {
        layoutSearchBar.visibility = View.INVISIBLE
        rvSubscriptions.visibility = View.INVISIBLE
        clNoSubscriptionsPlaceHolder.visibility = View.VISIBLE
        tvRecommendedTitle.visibility = View.VISIBLE
        rvCommunitiesRecommended.visibility = View.VISIBLE
    }

    private fun setExistSubscriptionsState() {
        layoutSearchBar.visibility = View.VISIBLE
        rvSubscriptions.visibility = View.VISIBLE
        clNoSubscriptionsPlaceHolder.visibility = View.INVISIBLE
        tvRecommendedTitle.visibility = View.INVISIBLE
        rvCommunitiesRecommended.visibility = View.INVISIBLE
    }
}
