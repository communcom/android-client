package io.golos.cyber_android.ui.screens.community_page_leaders_list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.databinding.FragmentCommunityLeadsBinding
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.LeadsListListAdapter
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model.LeadsListViewModel
import kotlinx.android.synthetic.main.fragment_community_leads.*

class LeadsListFragment : FragmentBaseMVVM<FragmentCommunityLeadsBinding, LeadsListViewModel>() {
    companion object {
        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun newInstance(communityId: String): LeadsListFragment =
            LeadsListFragment()
                .apply {
                    arguments = Bundle().apply { putString(ARG_COMMUNITY_ID, communityId) }
                }
    }

    private lateinit var leadsListListAdapter: LeadsListListAdapter
    private lateinit var leadsListLayoutManager: LinearLayoutManager

    override fun provideViewModelType(): Class<LeadsListViewModel> = LeadsListViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_leads

    override fun inject() = App.injections
        .get<CommunityPageLeadsListComponent>(arguments!!.getString(ARG_COMMUNITY_ID, io.golos.utils.EMPTY))
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunityPageLeadsListComponent>()
    }

    override fun linkViewModel(binding: FragmentCommunityLeadsBinding, viewModel: LeadsListViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    private fun updateList(data: List<VersionedListItem>) {
        if(!::leadsListListAdapter.isInitialized) {
            leadsListLayoutManager = LinearLayoutManager(context)

            leadsListListAdapter = LeadsListListAdapter(viewModel)
            leadsListListAdapter.setHasStableIds(true)

            mainList.isSaveEnabled = false
            mainList.itemAnimator = null
            mainList.layoutManager = leadsListLayoutManager
            mainList.adapter = leadsListListAdapter
        }

        leadsListListAdapter.update(data)
    }
}