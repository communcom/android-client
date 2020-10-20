package io.golos.cyber_android.ui.screens.communities_list.view

import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.shared.Tags.MENU
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

/**
 * Communities list for tabs on [MainActivity]
 */
class CommunitiesListFragmentTab : CommunitiesListFragment() {
    companion object {
        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = CommunitiesListFragmentTab().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) = App
        .injections.get<CommunitiesListFragmentTabComponent>(
            key,
            false,// show back button
            true,//show toolbar
            arguments!!.getParcelable<UserIdDomain>(USER_ID),       // user id
            true)                                                   // show all posts
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunitiesListFragmentTabComponent>(key)



    override fun showUnblockAndFollowDialog(communityId: CommunityIdDomain) {
        currentCommunity = communityId
        ConfirmationDialog.newInstance(R.string.this_community_is_in_your_blacklist,this@CommunitiesListFragmentTab)
            .show(requireFragmentManager(), MENU)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            ConfirmationDialog.REQUEST -> {
                if (resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.joinWithUnblock(currentCommunity)
                }
            }
        }
    }
}