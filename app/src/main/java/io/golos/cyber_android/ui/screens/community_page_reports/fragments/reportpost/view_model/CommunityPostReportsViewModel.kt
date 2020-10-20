package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.model.CommunityPostReportsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CommunityPostReportsViewModel
@Inject constructor(model: CommunityPostReportsModel, dispatchersProvider: DispatchersProvider) : ViewModelBase<CommunityPostReportsModel>(dispatchersProvider, model) {

    private val _posts = MutableLiveData<List<ReportedPostDomain>>()
    val posts: LiveData<List<ReportedPostDomain>>
        get() = _posts
    private val _noDataStubVisibility = MutableLiveData<Int>(View.GONE)
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility
    fun getPostReports() {
        launch {
            try {
                val data = model.getPostReports(40, 0)
                _posts.postValue(data)
                _noDataStubVisibility.value = if (data.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun onUserInHeaderClick(userId: String) {
        launch {
            try {
                _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = if (ex is ApiResponseErrorException && ex.message != null) {
                    ShowMessageTextCommand(ex.message!!)
                } else {
                    ShowMessageResCommand(R.string.common_general_error)
                }
            }
        }
    }

    fun onCommunityClicked(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

}