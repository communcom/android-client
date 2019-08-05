package io.golos.domain.interactors.user

import androidx.lifecycle.LiveData
import io.golos.domain.interactors.model.UserMetadataModel
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.UserMetadataRequest

interface UserMetadataUseCase {
    val getAsLiveData: LiveData<QueryResult<UserMetadataModel>>

    val getUpdateResultLiveData: LiveData<QueryResult<UserMetadataRequest>>

    fun requestRefresh()

    fun subscribe()

    fun updateMetadata(
        newBio: String? = null,
        newCoverUrl: String? = null,
        newProfileImageUrl: String? = null,
        shouldWaitForTransaction: Boolean = true
    )

    fun switchFollowingStatus()

    fun unsubscribe()
}