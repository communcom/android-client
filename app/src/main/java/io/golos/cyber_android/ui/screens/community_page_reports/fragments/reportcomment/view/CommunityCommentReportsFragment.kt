package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityReportsCommentsBinding
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.adapter.ReportAdapter
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.di.CommunityCommentReportsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view_model.CommunityCommentReportsViewModel
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view.CommunityPostReportsFragment
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view_model.CommunityPostReportsViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_reports_comments.*
import kotlinx.android.synthetic.main.fragment_community_reports_post.*
import kotlinx.android.synthetic.main.fragment_community_reports_post.rvReportsPost
import timber.log.Timber

class CommunityCommentReportsFragment : FragmentBaseMVVM<FragmentCommunityReportsCommentsBinding, CommunityCommentReportsViewModel>() {
    private val TAG = CommunityPostReportsFragment::class.java.simpleName

    override fun provideViewModelType(): Class<CommunityCommentReportsViewModel> = CommunityCommentReportsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_reports_comments

    override fun linkViewModel(binding: FragmentCommunityReportsCommentsBinding, viewModel: CommunityCommentReportsViewModel) {
        viewModel.getCommentReports()
        viewModel.commentReport.observe(viewLifecycleOwner, Observer {
            updateList(it,viewModel)
        })
    }
    override fun inject(key: String) {
        App.injections.get<CommunityCommentReportsFragmentComponent>(key).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<CommunityCommentReportsFragmentComponent>(key)
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
    fun updateList(data: List<ReportedPostDomain>, viewModel: CommunityCommentReportsViewModel) {

        val communitiesListLayoutManager = LinearLayoutManager(context)
        val allProposalsListAdapter = ReportAdapter(data,viewModel)
        allProposalsListAdapter.setHasStableIds(true)
        rvReportsComment.layoutManager = communitiesListLayoutManager
        rvReportsComment.adapter = allProposalsListAdapter

    }

}