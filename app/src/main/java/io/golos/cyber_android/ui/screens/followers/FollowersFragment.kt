package io.golos.cyber_android.ui.screens.followers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers.FollowersFragmentComponent
import io.golos.cyber_android.databinding.FragmentFollowersBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.utils.debounce
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.item_toolbar.*
import kotlinx.android.synthetic.main.view_search_bar.*

class FollowersFragment : FragmentBaseMVVM<FragmentFollowersBinding, FollowersViewModel>() {

    override fun releaseInjection() {
        App.injections.release<FollowersFragmentComponent>()
    }

    override fun layoutResId(): Int  = R.layout.fragment_followers

    private val adapter by lazy { FollowersAdapter() }

    override fun provideViewModelType(): Class<FollowersViewModel> = FollowersViewModel::class.java

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
        setupSearch()
        btnRetry.setOnClickListener {
            viewModel.start()
        }
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is BackCommand -> {
                fragmentManager?.popBackStack()
            }
        }
    }

    private fun setupSearch() {


        searchBar.addTextChangedListener(object : TextWatcher {

            private val querySearchListener: (String) -> Unit = debounce({
                viewModel.onFollowersSearchQueryChanged(it)
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
        viewModel.followersListStateLiveData.observe(this, Observer {
            when (it) {
                is Paginator.State.Data<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isFullData = false
                    adapter.isPageError = false
                    adapter.isNewPageProgress = false
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isFullData = true
                    adapter.isPageError = false
                    adapter.isNewPageProgress = false
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isPageError = true
                }
                is Paginator.State.NewPageProgress<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isPageError = false
                    adapter.isNewPageProgress = true
                }
                is Paginator.State.SearchProgress<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isNewPageProgress = true
                    pbLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    adapter.updateFollowers(it.data as MutableList<Follower>)
                    adapter.isNewPageProgress = false
                    uiHelper.showMessage(R.string.loading_error)
                    pbLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyProgress -> {
                    emptyPostProgressLoading.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                is Paginator.State.Empty -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    emptyPostProgressLoading.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        })

        viewModel.followingStatusLiveData.observe(this, Observer {
            adapter.updateFollowingStatus(it)
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
        adapter.onFollowClickedCallback = {
            viewModel.changeFollowingStatus(it)
        }
    }

    private fun setupToolbar() {
        toolbarTitle.setText(R.string.followers)
        ivBack.setOnClickListener {
            viewModel.back()
        }
    }

    companion object{

        fun newInstance(): FollowersFragment = FollowersFragment()
    }
}