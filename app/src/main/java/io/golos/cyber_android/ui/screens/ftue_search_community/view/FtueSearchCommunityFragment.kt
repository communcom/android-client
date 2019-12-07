package io.golos.cyber_android.ui.screens.ftue_search_community.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueSearchCommunityBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.mappers.mapToCommunityListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.FtueCommunityAdapter
import io.golos.cyber_android.ui.screens.ftue_search_community.viewmodel.FtueSearchCommunityViewModel
import kotlinx.android.synthetic.main.fragment_ftue_search_community.*

class FtueSearchCommunityFragment :
    FragmentBaseMVVM<FragmentFtueSearchCommunityBinding, FtueSearchCommunityViewModel>() {

    override fun provideViewModelType(): Class<FtueSearchCommunityViewModel> = FtueSearchCommunityViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_search_community

    override fun inject() = App.injections.get<FtueSearchCommunityFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueSearchCommunityFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueSearchCommunityBinding, viewModel: FtueSearchCommunityViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommunitiesList()
        observeViewModel()

        btnCommunityRetry.setOnClickListener {
            viewModel.onRetryLoadCommunity()
        }
    }

    private fun setupCommunitiesList() {
        val communityAdapter = FtueCommunityAdapter(viewModel)
        val lManager = GridLayoutManager(requireContext(), 2)
        lManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val cAdapter = rvCommunitiesList.adapter as FtueCommunityAdapter
                val isNeedToChangeSpan = cAdapter.isElementNotRetryOrError(position)
                return if (isNeedToChangeSpan) 2 else 1
            }
        }
        rvCommunitiesList.layoutManager = lManager
        rvCommunitiesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = lManager.itemCount
                val firstVisibleItem = lManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleItemCount) {
                    if (lManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1) {
                        viewModel.loadMoreCommunities()
                    }
                }
            }
        })

        rvCommunitiesList.adapter = communityAdapter
    }

    private fun observeViewModel() {
        viewModel.communityListState.observe(viewLifecycleOwner, Observer { state ->
            val cAdapter = rvCommunitiesList.adapter as FtueCommunityAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<Community>).mapToCommunityListItem()
                    cAdapter.update(items)
                }
                is Paginator.State.FullData<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.PageError<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.addRetry()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    cAdapter.addProgress()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.EmptyProgress -> {
                    btnCommunityRetry.visibility = View.VISIBLE
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                }
                is Paginator.State.Empty -> {
                    btnCommunityRetry.visibility = View.INVISIBLE
                    cAdapter.update(mutableListOf())
                }
                is Paginator.State.EmptyError -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnCommunityRetry.visibility = View.VISIBLE
                }
            }
        })
    }

    companion object {
        fun newInstance(): Fragment = FtueSearchCommunityFragment()
    }
}