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
        setupSearchCommunitiesObserver()
        setupCommunitiesRecommendedList()
        setupSubscriptionsList()
        viewModel.start()
    }

    private fun setupCommunitiesRecommendedList(){
        rvCommunitiesRecommended.layoutManager = LinearLayoutManager(requireContext())
        rvCommunitiesRecommended.adapter = subscriptionsAdapter
        subscriptionsAdapter.nextPageCallback = {
            viewModel.loadMoreRecommendedCommunities()
        }
        subscriptionsAdapter.onJoinClickedCallback = {
            viewModel.changeRecommendedCommunitySubscriptionStatus(it)
        }
    }

    private fun setupSubscriptionsList(){
        rvSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        rvSubscriptions.adapter = subscriptionsAdapter
        subscriptionsAdapter.nextPageCallback = {
            viewModel.loadMoreRecommendedCommunities()
        }
        subscriptionsAdapter.onJoinClickedCallback = {
            viewModel.changeRecommendedCommunitySubscriptionStatus(it)
        }
    }

    private fun setupSearchCommunitiesObserver(){
        (layoutSearchBar as EditText).addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    private fun updateListState(state: Paginator.State){
        when(state){
            is Paginator.State.Data<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = false
            }
            is Paginator.State.FullData<*> -> {
                subscriptionsAdapter.updateCommunities(state.data as MutableList<Community>)
                subscriptionsAdapter.isFullData = true
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
