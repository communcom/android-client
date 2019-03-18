package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import io.golos.domain.interactors.model.CommunityId

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface ServiceLocator {
    val getAppContext: Context

    fun getCommunityFeedViewModelFactory(communityId: CommunityId): ViewModelProvider.Factory

}