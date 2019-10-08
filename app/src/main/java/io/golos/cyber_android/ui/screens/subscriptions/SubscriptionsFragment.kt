package io.golos.cyber_android.ui.screens.subscriptions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions.SubscriptionsFragmentComponent
import io.golos.cyber_android.databinding.FragmentSubscriptionsBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToSearchCommunitiesCommand
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.android.synthetic.main.view_search_bar.*
import timber.log.Timber

class SubscriptionsFragment : FragmentBaseMVVM<FragmentSubscriptionsBinding, SubscriptionsModel, SubscriptionsViewModel>() {


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
        searchBar.addTextChangedListener(object: TextWatcher{

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
        viewModel.start()
    }

    private fun observeViewModel() {
        viewModel.subscriptionsState.observe(this, Observer {
            when (it) {
                SubscriptionsViewModel.SubscriptionsState.UNDEFINED -> setUndefinedSubscriptionsState()
                SubscriptionsViewModel.SubscriptionsState.EMPTY -> setEmptySubscriptionsState()
                SubscriptionsViewModel.SubscriptionsState.EXIST -> setExistSubscriptionsState()
                else -> { Timber.e("undefined subscription state type") }
            }
        })
        viewModel.command.observe(this, Observer {
            when(it) {
                is NavigateToSearchCommunitiesCommand -> {}
                is BackCommand -> {}
            }
        })
        viewModel.subscriptionsListState.observe(this, Observer {

        })
        viewModel.recommendedSubscriptionsListState.observe(this, Observer {

        })

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
