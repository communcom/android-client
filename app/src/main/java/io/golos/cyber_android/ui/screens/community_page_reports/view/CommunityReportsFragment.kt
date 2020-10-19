package io.golos.cyber_android.ui.screens.community_page_reports.view

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityReportsBinding
import io.golos.cyber_android.ui.screens.community_page_reports.adapter.ReportsPagerAdapter
import io.golos.cyber_android.ui.screens.community_page_reports.di.CommunityReportsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view.CommunityCommentReportsFragment
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view.CommunityPostReportsFragment
import io.golos.cyber_android.ui.screens.community_page_reports.view_model.CommunityReportsViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.TabLayoutMediator
import io.golos.domain.dto.CommunityIdDomain
import kotlinx.android.synthetic.main.fragment_community_reports.*


class CommunityReportsFragment : FragmentBaseMVVM<FragmentCommunityReportsBinding, CommunityReportsViewModel>() {

    companion object {
        private const val COMMUNITY_ID = "CommunityID"
        fun getInstance(communityIdDomain: CommunityIdDomain) = CommunityReportsFragment().apply {
            arguments = Bundle().apply { putParcelable(COMMUNITY_ID, communityIdDomain) }
        }
    }

    override fun provideViewModelType(): Class<CommunityReportsViewModel> {
        return CommunityReportsViewModel::class.java
    }

    override fun layoutResId(): Int {
        return R.layout.fragment_community_reports
    }

    override fun linkViewModel(binding: FragmentCommunityReportsBinding, viewModel: CommunityReportsViewModel) {
        val items = initPagerItems()
        binding.apply {
            vpReports.adapter = ReportsPagerAdapter(childFragmentManager, lifecycle, items)
            vpReports.offscreenPageLimit = items.size
            TabLayoutMediator(reportsTabs, vpReports) { tab, position ->
                tab.text = items[position].title
                vpReports.setCurrentItem(0, false)
            }.attach()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBack.setOnClickListener {
            viewModel.onBackPressed()
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is NavigateBackwardCommand -> requireFragmentManager().popBackStack()
        }
    }

    override fun inject(key: String) {
        App.injections.get<CommunityReportsFragmentComponent>(key, arguments?.getParcelable(COMMUNITY_ID)!!).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<CommunityReportsFragmentComponent>(key)
    }

    private fun initPagerItems(): ArrayList<ReportsPagerAdapter.FragmentPagerModel> {
        return arrayListOf(ReportsPagerAdapter.FragmentPagerModel(CommunityPostReportsFragment(), resources.getString(R.string.discovery_posts)), ReportsPagerAdapter.FragmentPagerModel(CommunityCommentReportsFragment(), resources.getString(R.string.comments)))

    }

}