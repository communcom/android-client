package io.golos.cyber_android.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.ui.screens.feed.FeedPageViewModel
import io.golos.cyber_android.ui.screens.profile.edit.BaseEditProfileViewModel
import io.golos.data.errors.AppError
import io.golos.domain.interactors.model.UserMetadataModel
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.QueryResult

class ProfileViewModel(
    private val userMetadataUseCase: UserMetadataUseCase,
    private val signInUseCase: SignInUseCase,
    internal val forUser: CyberName
) : BaseEditProfileViewModel(userMetadataUseCase) {

    data class Profile(val metadata: UserMetadataModel, val isActiveUserProfile: Boolean) {
        companion object {
            val EMPTY = Profile(UserMetadataModel.empty, false)
            val NOT_FOUND = Profile(UserMetadataModel.empty, true)
        }
    }

    /**
     * [LiveData] that indicates if profile of this view model is the actual profile of an app user
     */
    private val getMyUserLiveData = signInUseCase.getAsLiveData.map {
        it?.userName == forUser
    }


    private val profileLiveData = MediatorLiveData<QueryResult<Profile>>().apply {
        var metadata: QueryResult<UserMetadataModel>? = null
        var isMyUser: Boolean? = null
        addSource(getMetadataLiveData) { metadataResult ->
            metadata = metadataResult
            isMyUser?.let { isMyUserResult ->
                postProfileData(metadataResult, isMyUserResult)
            }
        }

        addSource(getMyUserLiveData) { isMyUserResult ->
            isMyUser = isMyUserResult
            metadata?.let { metadataResult ->
                postProfileData(metadataResult, isMyUserResult)
            }
        }
    }

    /**
     * [LiveData] for profile.
     */
    val getProfileLiveData: LiveData<QueryResult<Profile>> = profileLiveData

    fun clearProfileCover() {
        userMetadataUseCase.updateMetadata(newCoverUrl = "")
    }

    fun clearProfileAvatar() {
        userMetadataUseCase.updateMetadata(newProfileImageUrl = "")
    }

    private val eventsLiveData = MutableLiveData<FeedPageViewModel.Event>()

    val getEventsLiveData = eventsLiveData as LiveData<FeedPageViewModel.Event>

    init {
        signInUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
    }

    fun requestRefresh() {
        userMetadataUseCase.requestRefresh()
        eventsLiveData.postValue(FeedPageViewModel.Event.RefreshRequestEvent)
    }

    fun switchFollowingStatus() {
        userMetadataUseCase.switchFollowingStatus()
    }
}


private fun MediatorLiveData<QueryResult<ProfileViewModel.Profile>>.postProfileData(
    metadataResult: QueryResult<UserMetadataModel>?,
    isMyUserResult: Boolean
) {
    when (metadataResult) {
        is QueryResult.Success -> postValue(
            QueryResult.Success(
                ProfileViewModel.Profile(
                    metadataResult.originalQuery,
                    isMyUserResult
                )
            )
        )
        is QueryResult.Error -> when {
            metadataResult.error is AppError.NotFoundError ->
                postValue(QueryResult.Error(metadataResult.error, ProfileViewModel.Profile.NOT_FOUND))
            else ->
                postValue(QueryResult.Error(metadataResult.error, ProfileViewModel.Profile.EMPTY))
        }
        is QueryResult.Loading -> postValue(QueryResult.Loading(ProfileViewModel.Profile.EMPTY))
    }
}