package io.golos.cyber_android.ui.screens.subscriptions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.android.synthetic.main.item_content_embed.*
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
        subscriptionsAdapter.onJoinClickedCallback = {
            viewModel.changeCommunitySubscriptionStatus(it)
        }
    }

    private fun setupSearch() {
        searchBar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                //implement if need
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //implement if need
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onCommunitySearchQueryChanged(s.toString())
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
    }

    private fun updateListState(state: Paginator.State) {
        when (state) {
            is Paginator.State.Data<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = false
                pbLoading.visibility = View.INVISIBLE
            }
            is Paginator.State.FullData<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = true
                pbLoading.visibility = View.INVISIBLE
            }
            is Paginator.State.SearchProgress<*> -> {
                pbLoading.visibility = View.VISIBLE
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
