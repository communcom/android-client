package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.shared.recycler_view.versioned.paging.LoadedItemsList
import io.golos.domain.dto.UserIdDomain

interface UsersListWorker: LoadedItemsList {
    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(userId: UserIdDomain): Boolean

    /**
     * Subscribe/unsubscribe without network call
     */
    fun subscribeUnsubscribeInstant(userId: UserIdDomain)
}