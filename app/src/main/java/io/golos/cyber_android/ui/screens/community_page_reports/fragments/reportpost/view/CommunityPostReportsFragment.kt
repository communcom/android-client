package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityReportsPostBinding
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.adapter.ReportAdapter

import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.di.CommunityPostReportsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view_model.CommunityPostReportsViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_reports_post.*
import timber.log.Timber

class CommunityPostReportsFragment : FragmentBaseMVVM<FragmentCommunityReportsPostBinding, CommunityPostReportsViewModel>() {
    private val TAG = CommunityPostReportsFragment::class.java.simpleName

    override fun provideViewModelType(): Class<CommunityPostReportsViewModel> = CommunityPostReportsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_reports_post

    override fun linkViewModel(binding: FragmentCommunityReportsPostBinding, viewModel: CommunityPostReportsViewModel) {
        viewModel.getPostReports()
        viewModel.posts.observe(viewLifecycleOwner, Observer {
            updateList(it,viewModel)
        })
    }

    override fun inject(key: String) {
        App.injections.get<CommunityPostReportsFragmentComponent>(key).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<CommunityPostReportsFragmentComponent>(key)
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
        }
    }
    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }
    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(CommunityPageFragment.newInstance(communityId))
    }
    fun updateList(data: List<ReportedPostDomain>, viewModel: CommunityPostReportsViewModel) {

        val communitiesListLayoutManager = LinearLayoutManager(context)
        val allProposalsListAdapter = ReportAdapter(data,viewModel)
        allProposalsListAdapter.setHasStableIds(true)
        rvReportsPost.layoutManager = communitiesListLayoutManager
        rvReportsPost.adapter = allProposalsListAdapter

    }
}