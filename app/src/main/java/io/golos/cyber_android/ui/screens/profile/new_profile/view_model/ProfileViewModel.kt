package io.golos.cyber_android.ui.screens.profile.new_profile.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.profile.new_profile.model.ProfileModel
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ProfileViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileModel
) : ViewModelBase<ProfileModel>(dispatchersProvider, model) {

    private val _coverUrl: MutableLiveData<String?> = MutableLiveData("https://media.istockphoto.com/vectors/fashionable-pattern-in-the-arab-style-seamless-background-arabesque-vector-id928387200")
    val coverUrl: LiveData<String?> get() = _coverUrl

    private val _avatarUrl: MutableLiveData<String?> = MutableLiveData("http://www.born-today.com/images/1391266842p5/2742325.jpg")
    val avatarUrl: LiveData<String?> get() = _avatarUrl

    private val _name: MutableLiveData<String?> = MutableLiveData("Ghiyath al-Din Abu'l-Fath Umar ibn Ibrahim Al-Nisaburi al-Khayyami")
    val name: LiveData<String?> get() = _name

    private val _joinDate: MutableLiveData<Date> = MutableLiveData(Date())
    val joinDate: LiveData<Date> get() = _joinDate

    private val _bio: MutableLiveData<String?> = MutableLiveData("Omar Khayyam was a Persian mathematician, astronomer, and poet. He was born in Nishapur, in northeastern Iran, and spent most of his life near the court of the Karakhanid and Seljuq rulers in the period which witnessed the First Crusade.")
    val bio: LiveData<String?> get() = _bio

    private val _bioVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val bioVisibility: LiveData<Int> get() = _bioVisibility

    private val _addBioVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val addBioVisibility: LiveData<Int> get() = _addBioVisibility

    private val _followersCount: MutableLiveData<Long> = MutableLiveData(0)
    val followersCount: LiveData<Long> get() = _followersCount

    private val _followingsCount: MutableLiveData<Long> = MutableLiveData(0)
    val followingsCount: LiveData<Long> get() = _followingsCount

    private val _communitiesVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val communitiesVisibility: LiveData<Int> get() = _communitiesVisibility

    private val _pageContentVisibility: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)
    val pageContentVisibility: LiveData<Int> get() = _pageContentVisibility

    private val _retryButtonVisibility: MutableLiveData<Int> = MutableLiveData(View.INVISIBLE)
    val retryButtonVisibility: LiveData<Int> get() = _retryButtonVisibility

    private val _loadingProgressVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val loadingProgressVisibility: LiveData<Int> get() = _loadingProgressVisibility

    fun start() = loadPage()

    fun onRetryClick() {
        _retryButtonVisibility.value = View.INVISIBLE
        _loadingProgressVisibility.value = View.VISIBLE
        loadPage()
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
                    _bioVisibility.value = if(bio.isNullOrEmpty()) View.GONE else View.VISIBLE
                    _addBioVisibility.value = if(bio.isNullOrEmpty()) View.VISIBLE else View.GONE
                    _followersCount.value = followersCount
                    _followingsCount.value = followingsCount
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
}