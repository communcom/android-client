package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class DiscoveryUsersFragmentModule(
    private val pageSize: Int,
    private val isLimited:Boolean
){
    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize

    @Provides
    fun providesListType():Boolean = isLimited
}