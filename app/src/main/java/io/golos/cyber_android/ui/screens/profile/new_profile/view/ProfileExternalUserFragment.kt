package io.golos.cyber_android.ui.screens.profile.new_profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile.new_profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.view.adapters.ProfilePagesExternalUserAdapter
import io.golos.cyber_android.ui.screens.profile_communities.view.ProfileCommunitiesExternalUserFragment
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersExternalUserFragment
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

class ProfileExternalUserFragment : ProfileFragment() {
    companion object {
        fun newInstance(userId: UserIdDomain) = ProfileExternalUserFragment().apply {
            arguments = Bundle().apply { putParcelable(Tags.USER_ID, userId) }
        }
    }

    override fun inject() =
        App.injections.get<ProfileExternalUserFragmentComponent>(arguments!!.getParcelable<UserIdDomain>(Tags.USER_ID)).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileExternalUserFragmentComponent>()
    }

    override fun provideCommunitiesFragment(sourceData: ProfileCommunities): Fragment =
        ProfileCommunitiesExternalUserFragment.newInstance(sourceData)

    override fun provideFollowersFragment(filter: FollowersFilter, mutualUsers: List<UserDomain>): Fragment =
        ProfileFollowersExternalUserFragment.newInstance(filter, mutualUsers)

    override fun providePagesAdapter(): FragmentPagerAdapter = ProfilePagesExternalUserAdapter(
        context!!.applicationContext,
        getDashboardFragment(this)!!.childFragmentManager
    )
}