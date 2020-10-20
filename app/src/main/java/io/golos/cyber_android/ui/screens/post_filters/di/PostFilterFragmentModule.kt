package io.golos.cyber_android.ui.screens.post_filters.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class PostFilterFragmentModule(
    private val isNeedToSaveGlobalState: Boolean,
    private val timeFilter: PostFiltersHolder.UpdateTimeFilter?,
    private val periodFilter: PostFiltersHolder.PeriodTimeFilter?
) {

    @Provides
    @Named(value = Clarification.FILTER_GLOBAL)
    internal fun provideIsNeedToSaveGlobalState(): Boolean = isNeedToSaveGlobalState

    @Provides
    @Named(value = Clarification.FILTER_TIME)
    internal fun provideTimeFilter(): PostFiltersHolder.UpdateTimeFilter? = timeFilter

    @Provides
    @Named(value = Clarification.FILTER_PERIOD)
    internal fun providePeriodFilter(): PostFiltersHolder.PeriodTimeFilter? = periodFilter

}