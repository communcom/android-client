package io.golos.cyber_android.ui.screens.profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile.di.ProfileExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile.view.adapters.ProfilePagesExternalUserAdapter
import io.golos.cyber_android.ui.screens.profile_communities.view.ProfileCommunitiesExternalUserFragment
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersExternalUserFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain

class ProfileExternalUserFragment : ProfileFragment() {
    companion object {
        fun newInstance(userId: UserIdDomain) = ProfileExternalUserFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Tags.USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) =
        App.injections.get<ProfileExternalUserFragmentComponent>(
            key,
            arguments!!.getParcelable<UserIdDomain>(Tags.USER_ID)
        ).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileExternalUserFragmentComponent>(key)

    override fun provideCommunitiesFragment(sourceData: ProfileCommunities): Fragment =
        ProfileCommunitiesExternalUserFragment.newInstance(sourceData)

    override fun provideFollowersFragment(filter: FollowersFilter, mutualUsers: List<UserDomain>): Fragment =
        ProfileFollowersExternalUserFragment.newInstance(filter, mutualUsers)

    override fun providePagesAdapter(): FragmentStatePagerAdapter = ProfilePagesExternalUserAdapter(
        context!!.applicationContext,
        childFragmentManager,
        arguments?.getParcelable(Tags.USER_ID)!!
    )
}