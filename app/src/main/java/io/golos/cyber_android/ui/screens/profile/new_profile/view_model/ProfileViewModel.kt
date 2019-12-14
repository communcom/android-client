package io.golos.cyber_android.ui.screens.profile.new_profile.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowConfirmationDialog
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.mappers.mapToCommunity
import io.golos.cyber_android.ui.screens.profile.new_profile.dto.*
import io.golos.cyber_android.ui.screens.profile.new_profile.model.ProfileModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.UnsupportedOperationException
import java.util.*
import javax.inject.Inject

class ProfileViewModel
@Inject
constructor(
    val profileUserId: UserIdDomain,
    dispatchersProvider: DispatchersProvider,
    model: ProfileModel
) : ViewModelBase<ProfileModel>(dispatchersProvider, model) {

    private val _coverUrl: MutableLiveData<String?> = MutableLiveData()
    val coverUrl: LiveData<String?> get() = _coverUrl

    private val _avatarUrl: MutableLiveData<String?> = MutableLiveData()
    val avatarUrl: LiveData<String?> get() = _avatarUrl

    private val _name: MutableLiveData<String?> = MutableLiveData()
    val name: LiveData<String?> get() = _name

    private val _joinDate: MutableLiveData<Date> = MutableLiveData(Date())
    val joinDate: LiveData<Date> get() = _joinDate

    private val _bio: MutableLiveData<String?> = MutableLiveData()
    val bio: LiveData<String?> get() = _bio

    private val _bioVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val bioVisibility: LiveData<Int> get() = _bioVisibility

    private val _addBioVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val addBioVisibility: LiveData<Int> get() = _addBioVisibility

    private val _followersCount: MutableLiveData<Int> = MutableLiveData(0)
    val followersCount: LiveData<Int> get() = _followersCount

    private val _followingsCount: MutableLiveData<Int> = MutableLiveData(0)
    val followingsCount: LiveData<Int> get() = _followingsCount

    private val _communities: MutableLiveData<ProfileCommunities?> = MutableLiveData(null)
    val communities: LiveData<ProfileCommunities?> get() = _communities

    private val _pageContentVisibility: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)
    val pageContentVisibility: LiveData<Int> get() = _pageContentVisibility

    private val _retryButtonVisibility: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)
    val retryButtonVisibility: LiveData<Int> get() = _retryButtonVisibility

    private val _loadingProgressVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val loadingProgressVisibility: LiveData<Int> get() = _loadingProgressVisibility

    val backButtonVisibility = if(model.isCurrentUser) View.INVISIBLE else View.VISIBLE
    val followButtonVisibility = if(model.isCurrentUser) View.GONE else View.VISIBLE
    val photoButtonsVisibility = if(model.isCurrentUser) View.VISIBLE else View.INVISIBLE

    private var bioUpdateInProgress = false

    init {
        _bio.observeForever {
            _bioVisibility.value = if(it.isNullOrEmpty()) View.GONE else View.VISIBLE

            _addBioVisibility.value =
                if(!model.isCurrentUser) {
                    View.GONE
                } else {
                    if(it.isNullOrEmpty())
                        View.VISIBLE
                    else
                        View.GONE
                }
        }
    }

    fun start() = loadPage()

    fun onRetryClick() {
        _retryButtonVisibility.value = View.INVISIBLE
        _loadingProgressVisibility.value = View.VISIBLE
        loadPage()
    }

    fun onUpdatePhotoClick(place: ProfileItem) {
        _command.value = ShowSelectPhotoDialogCommand(place)
    }

    fun onSelectMenuChosen(item: ProfileItem) {
        _command.value = when(item) {
            ProfileItem.AVATAR -> MoveToSelectPhotoPageCommand(item, model.avatarUrl)
            ProfileItem.COVER -> MoveToSelectPhotoPageCommand(item, model.coverUrl)
            ProfileItem.BIO -> MoveToBioPageCommand(_bio.value)
        }
    }

    fun onDeleteMenuChosen(place: ProfileItem) {
        launch {
            when(place) {
                ProfileItem.COVER -> deleteCover()
                ProfileItem.AVATAR -> deleteAvatar()
                ProfileItem.BIO -> deleteBio()
            }
        }
    }

    fun onAddBioClick() {
        _command.value = MoveToBioPageCommand(null)
    }

    fun updatePhoto(imageFile: File, item: ProfileItem) {
        launch {
            when(item) {
                ProfileItem.COVER -> updateCover(imageFile)
                ProfileItem.AVATAR -> updateAvatar(imageFile)
                else -> throw UnsupportedOperationException("This item is not supported: $item")
            }
        }
    }

    fun updateBio(text: String) {
        val oldValue = _bio.value

        launch {
            try {
                bioUpdateInProgress = true

                _bio.value = text

                model.sendBio(text)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)

                _bio.value = oldValue
            } finally {
                bioUpdateInProgress = false
            }
        }
    }

    fun onBioClick() {
        if(bioUpdateInProgress || !model.isCurrentUser) {
            return
        }
        _command.value = ShowEditBioDialogCommand()
    }

    fun onFollowersClick() {
        _command.value = MoveToFollowersPageCommand(FollowersFilter.FOLLOWERS, model.mutualUsers)
    }

    fun onFollowingsClick() {
        _command.value = MoveToFollowersPageCommand(FollowersFilter.FOLLOWINGS, model.mutualUsers)
    }

    fun onSettingsClick() {
        _command.value = ShowSettingsDialogCommand()
    }

    fun onLogoutSelected() {
        _command.value = ShowConfirmationDialog(R.string.log_out_question)
    }

    fun onLikedSelected() {
        _command.value = MoveToLikedPageCommand()
    }

    fun onBlackListSelected() {
        _command.value = MoveToBlackListPageCommand()
    }

    fun onLogoutConfirmed() {
        launch {
            var isSuccess = true

            _command.value = SetLoadingVisibilityCommand(true)
            try {
                model.logout()
            } catch (ex: Exception) {
                isSuccess = false
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }

            if(isSuccess) {
                model.restartApp()
            }
        }
    }

    private fun loadPage() {
        launch {
            try {
                with(model.loadProfileInfo()) {
                    _coverUrl.value = coverUrl
                    _avatarUrl.value = avatarUrl
                    _name.value = name
                    _joinDate.value = joinDate
                    _bio.value = bio
                    _followersCount.value = followersCount
                    _followingsCount.value = followingsCount

                    if(highlightCommunities.isNotEmpty()) {
                        _communities.value = ProfileCommunities(communitiesSubscribedCount, highlightCommunities.map { it.mapToCommunity() })
                    }
                }

                _pageContentVisibility.value = View.VISIBLE

            } catch (ex: Exception) {
                Timber.e(ex)
                _retryButtonVisibility.value = View.VISIBLE
            } finally {
                _loadingProgressVisibility.value = View.INVISIBLE
            }
        }
    }

    private suspend fun updateAvatar(avatarFile: File) {
        val oldValue = _avatarUrl.value

        try {
            _avatarUrl.value = avatarFile.toURI().toString()
            model.sendAvatar(avatarFile)
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageCommand(R.string.common_general_error)
            _avatarUrl.value = oldValue
        }
    }

    private suspend fun updateCover(coverFile: File) {
        val oldValue = _coverUrl.value

        try {
            _coverUrl.value = coverFile.toURI().toString()
            model.sendCover(coverFile)
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageCommand(R.string.common_general_error)
            _coverUrl.value = oldValue
        }
    }

    private suspend fun deleteAvatar() {
        val oldValue = _avatarUrl.value
        try {
            _avatarUrl.value = null
            model.clearAvatar()
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageCommand(R.string.common_general_error)
            _avatarUrl.value = oldValue
        }
    }

    private suspend fun deleteCover() {
        val oldValue = _coverUrl.value
        try {
            _coverUrl.value = null
            model.clearCover()
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageCommand(R.string.common_general_error)
            _coverUrl.value = oldValue
        }
    }

    private fun deleteBio() {
        val oldValue = _bio.value

        launch {
            try {
                bioUpdateInProgress = true

                _bio.value = null

                model.clearBio()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)

                _bio.value = oldValue
            } finally {
                bioUpdateInProgress = false
            }
        }
    }
}