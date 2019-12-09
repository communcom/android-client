package io.golos.cyber_android.ui.screens.ftue_search_community.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueSearchCommunityBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.mappers.mapToCollectionListItem
import io.golos.cyber_android.ui.mappers.mapToCommunityListItem
import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.collection.FtueCommunityCollectionAdapter
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.community.FtueCommunityAdapter
import io.golos.cyber_android.ui.screens.ftue_search_community.viewmodel.FtueSearchCommunityViewModel
import io.golos.cyber_android.ui.utils.debounce
import kotlinx.android.synthetic.main.fragment_ftue_search_community.*
import kotlinx.android.synthetic.main.view_search_bar.*

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
        setupCommunityCollectionList()
        observeViewModel()
        setupSearchCommunities()
        btnRetry.setOnClickListener {
            viewModel.onRetryLoadCommunity()
        }
        btnNext.setOnClickListener {
            viewModel.sendCommunitiesCollection()
        }

    }

    private fun setupSearchCommunities(){
        etSearch.addTextChangedListener(object : TextWatcher {

            private val querySearchListener: (String) -> Unit = debounce({
                viewModel.onCommunitiesSearchQueryChanged(it)
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

    private fun setupCommunitiesList() {
        val communityAdapter =
            FtueCommunityAdapter(viewModel)
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

    private fun setupCommunityCollectionList() {
        val collectionAdapter = FtueCommunityCollectionAdapter(viewModel)
        val lManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCommunitiesCollection.layoutManager = lManager
        rvCommunitiesCollection.adapter = collectionAdapter
    }

    private fun observeViewModel() {
        viewModel.communityListState.observe(viewLifecycleOwner, Observer { state ->
            val cAdapter = rvCommunitiesList.adapter as FtueCommunityAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<FtueCommunityListItem>)
                    cAdapter.update(items)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                    emptyProgressLoading.visibility = View.INVISIBLE
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.addRetry()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.NewPageProgress<*> -> {
                    cAdapter.addProgress()
                    rvCommunitiesList.scrollToPosition(cAdapter.itemCount - 1)
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyProgress -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.VISIBLE
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.Empty -> {
                    cAdapter.update(mutableListOf())
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.VISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                    pbSearchLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.SearchProgress<*> -> {
                    cAdapter.update((state.data as MutableList<FtueCommunityListItem>))
                    pbSearchLoading.visibility = View.VISIBLE
                }
                is Paginator.State.SearchPageError<*> -> {
                    cAdapter.update((state.data as MutableList<FtueCommunityListItem>))
                    uiHelper.showMessage(R.string.loading_error)
                    pbSearchLoading.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.collectionListState.observe(viewLifecycleOwner, Observer { listOfCommunities ->
            val cAdapter = rvCommunitiesCollection.adapter as FtueCommunityCollectionAdapter
            cAdapter.update(listOfCommunities)
        })

        viewModel.haveNeedCollection.observe(viewLifecycleOwner, Observer { isEnabled ->
            btnNext.isEnabled = isEnabled
        })
    }

    companion object {
        fun newInstance(): Fragment = FtueSearchCommunityFragment()
    }
}