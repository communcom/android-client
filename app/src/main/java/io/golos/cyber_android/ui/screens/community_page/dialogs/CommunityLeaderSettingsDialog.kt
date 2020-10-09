package io.golos.cyber_android.ui.screens.community_page.dialogs

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityInfo
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.domain.dto.CommunityDomain
import io.golos.utils.format.size.PluralSizeFormatter
import kotlinx.android.synthetic.main.dialog_community_leader_settings.*
import kotlinx.android.synthetic.main.view_communities_community_list_item.view.*

class CommunityLeaderSettingsDialog : BottomSheetDialogFragmentBase<CommunityLeaderSettingsDialog.Result>() {

    companion object {
        fun show(fragmentManager: FragmentManager, communityPage: CommunityInfo, closeActionListener: (Result?) -> Unit) =
            CommunityLeaderSettingsDialog().apply {
                this.closeActionListener = closeActionListener
                this.communityPage = communityPage
            }.show(fragmentManager, "LEADER_DASHBOARD")
    }

    private var communityPage: CommunityInfo? = null

    sealed class Result {
        object Reports : Result()
        object Proposals : Result()
        object Members : Result()
        object BlockedUsers : Result()
        object Settings : Result()
    }

    override val closeButton: View?
        get() = communityLeaderSettingsClose
    override val layout: Int
        get() = R.layout.dialog_community_leader_settings

    override fun setupView() {
        val followersFormatter = PluralSizeFormatter(requireContext(), R.plurals.formatter_followers_formatted)
        val postsFormatter = PluralSizeFormatter(requireContext(), R.plurals.formatter_posts_formatted)
        communityPage?.let {
            val followers = followersFormatter.format(it.subscribersCount)
            val posts = postsFormatter.format(it.postsCount)

            communityTitle.text = it.name

            communityInfo.text = "$followers ${SpecialChars.BULLET} $posts"

            ivLogo.loadCommunity(it.avatarUrl)
            vReportCount.text = "+${it.reportCount}"
            vProposalsCount.text = "+${it.proposalCount}"
        }
        tvReports.setOnClickListener { closeOnItemSelected(Result.Reports) }
        tvBlockedUsers.setOnClickListener { closeOnItemSelected(Result.BlockedUsers) }
        tvSettings.setOnClickListener { closeOnItemSelected(Result.Settings) }
        tvMembers.setOnClickListener { closeOnItemSelected(Result.Members) }
        tvProposals.setOnClickListener { closeOnItemSelected(Result.Proposals) }
    }

}