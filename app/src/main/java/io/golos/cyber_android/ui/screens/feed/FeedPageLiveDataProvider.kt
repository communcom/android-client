package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData

interface FeedPageLiveDataProvider {
    fun provideEventsLiveData(): LiveData<FeedPageViewModel.Event>
}