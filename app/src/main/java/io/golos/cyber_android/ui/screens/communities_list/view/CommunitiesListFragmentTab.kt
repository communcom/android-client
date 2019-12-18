package io.golos.cyber_android.ui.screens.communities_list.view

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
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

    override fun inject() = App
        .injections.get<CommunitiesListFragmentTabComponent>(
            false,                                                  // show back button
            arguments!!.getParcelable<UserIdDomain>(USER_ID),       // user id
            true)                                                   // show all posts
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunitiesListFragmentTabComponent>()
    }
}