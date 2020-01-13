package io.golos.cyber_android.ui.screens.profile_posts.view

import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent
import io.golos.domain.dto.PostsConfigurationDomain

class ProfilePostsLikedFragment : ProfilePostsFragment() {
    override fun inject() {
        App.injections
            .get<ProfilePostsLikedFragmentComponent>(PostsConfigurationDomain.TypeFeedDomain.NEW)
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<ProfilePostsLikedFragmentComponent>()
    }
}