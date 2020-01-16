package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain

interface UsersListWorker {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(userId: UserIdDomain): Boolean

    /**
     * Subscribe/unsubscribe without network call
     */
    fun subscribeUnsubscribeInstant(userId: UserIdDomain)
}