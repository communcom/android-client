package io.golos.cyber_android.ui.screens.profile_communities.view

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesExternalUserFragmentComponent

class ProfileCommunitiesExternalUserFragment : ProfileCommunitiesFragment() {
    companion object {
        private const val SOURCE_DATA = "SOURCE_DATA"

        fun newInstance(sourceData: ProfileCommunities) =
            ProfileCommunitiesExternalUserFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SOURCE_DATA, sourceData)
                }
            }
    }

    override fun inject() =
        App.injections
            .get<ProfileCommunitiesExternalUserFragmentComponent>(arguments!!.getParcelable<ProfileCommunities>(SOURCE_DATA))
            .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommunitiesExternalUserFragmentComponent>()
    }
}