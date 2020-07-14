package io.golos.cyber_android.ui.screens.profile_posts.view

import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent
import io.golos.domain.dto.PostsConfigurationDomain

class ProfilePostsLikedFragment : ProfilePostsFragment() {
    override fun inject(key: String) {
        App.injections
            .get<ProfilePostsLikedFragmentComponent>(key, PostsConfigurationDomain.TypeFeedDomain.VOTED)
            .inject(this)
    }

    override fun releaseInjection(key: String) = App.injections.release<ProfilePostsLikedFragmentComponent>(key)
}