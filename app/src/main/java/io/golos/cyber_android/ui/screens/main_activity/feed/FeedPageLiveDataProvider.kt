package io.golos.cyber_android.ui.screens.main_activity.feed

import androidx.lifecycle.LiveData

interface FeedPageLiveDataProvider {
    fun provideEventsLiveData(): LiveData<FeedPageViewModel.Event>
}