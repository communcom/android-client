package io.golos.cyber_android.ui.screens.profile_black_list.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class ProfileBlackListFragmentModule(
    private val filter: BlackListFilter,
    private val pageSize: Int) {

    @Provides
    fun provideBlackListFilter(): BlackListFilter = filter

    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize
}