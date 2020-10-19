package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.model.CommunityCommentReportsModel
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.model.CommunityPostReportsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
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

class CommunityCommentReportsViewModel
@Inject constructor(model: CommunityCommentReportsModel, dispatchersProvider: DispatchersProvider) : ViewModelBase<CommunityCommentReportsModel>(dispatchersProvider, model) {

    private val _comment = MutableLiveData<List<ReportedPostDomain>>()
    val commentReport: LiveData<List<ReportedPostDomain>>
        get() = _comment
    private val _noDataStubVisibility = MutableLiveData<Int>(View.GONE)
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility
    fun getCommentReports() {
        launch {
            try {
                val data = model.getCommentReports(40, 0)
                _comment.postValue(data)
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