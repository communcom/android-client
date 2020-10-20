package io.golos.cyber_android.ui.screens.community_page_friends.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend

@Module
class CommunityPageFriendsModule(
    private val friends: List<CommunityFriend>
) {
    @Provides
    fun provideFriends(): List<CommunityFriend> = friends
}