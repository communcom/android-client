package io.golos.cyber_android.ui.screens.community_page_reports.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.services.model.GetReportsResponse
import io.golos.cyber_android.ui.screens.community_page_reports.model.CommunityReportsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.ReportedPostDomain
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CommunityReportsViewModel
@Inject constructor(model: CommunityReportsModel, dispatchersProvider: DispatchersProvider) : ViewModelBase<CommunityReportsModel>(dispatchersProvider, model) {

    private val _posts = MutableLiveData<List<ReportedPostDomain>>()
    val posts: LiveData<List<ReportedPostDomain>>
        get() = _posts

    private val _comments = MutableLiveData<List<ReportedPostDomain>>()
    val comments: LiveData<List<ReportedPostDomain>>
        get() = _comments

    fun getPostReports() {
        launch {
            try {
                val a = model.getPostReports(40, 0)
                _posts.postValue(a)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /*    fun getCommentReports() {
            launch {
                val a = model.getCommentReports(40, 0)
                _comments.postValue(a)
            }
        }*/

    fun onBackPressed() {
        _command.value = NavigateBackwardCommand()
    }

}