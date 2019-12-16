package io.golos.cyber_android.ui.screens.profile_posts.view

import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsExternalUserFragmentComponent
import io.golos.domain.dto.PostsConfigurationDomain

class ProfilePostsExternalUserFragment : ProfilePostsFragment() {
    companion object {
        fun newInstance() = ProfilePostsExternalUserFragment()
    }

    override fun inject() {
        App.injections
            .get<ProfilePostsExternalUserFragmentComponent>(PostsConfigurationDomain.TypeFeedDomain.BY_USER)
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<ProfilePostsExternalUserFragmentComponent>()
    }
}