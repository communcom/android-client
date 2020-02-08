package io.golos.cyber_android.ui.screens.profile_followers.view

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersExternalUserFragmentComponent
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserDomain

class ProfileFollowersExternalUserFragment : ProfileFollowersFragment(){
    companion object {
        private const val FILTER = "FILTER"
        private const val MUTUAL_USERS = "MUTUAL_USERS"

        fun newInstance(filter: FollowersFilter, mutualUsers: List<UserDomain>) = ProfileFollowersExternalUserFragment().apply {
            arguments = Bundle().apply {
                putInt(FILTER, filter.value)
                putParcelableArray(MUTUAL_USERS, mutualUsers.toTypedArray())
            }
        }
    }

    override fun inject(key: String) =
        App.injections
            .get<ProfileFollowersExternalUserFragmentComponent>(
                key,
                FollowersFilter.create(arguments!!.getInt(FILTER)),
                GlobalConstants.PAGE_SIZE,
                arguments!!.getParcelableArray(MUTUAL_USERS)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileFollowersExternalUserFragmentComponent>(key)
}