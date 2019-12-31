package io.golos.cyber_android.ui.screens.main_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent

@Module(subcomponents = [
    MyFeedFragmentComponent::class,
    CommunitiesListFragmentComponent::class,
    CommunitiesListFragmentTabComponent::class
])
class MainActivityModuleChilds