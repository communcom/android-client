package io.golos.cyber_android.ui.screens.wallet_send_points.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectUserDialog
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UserInfo
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserDomain
import javax.inject.Inject

class WalletSendPointsViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletSendPointsModel
) : ViewModelBase<WalletSendPointsModel>(dispatchersProvider, model) {

    private val _userInfo = MutableLiveData<UserInfo>(getUserInfo(model.sendToUser))
    val userInfo: LiveData<UserInfo> = _userInfo

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSelectUserClick() {
        _command.value = ShowSelectUserDialog()
    }

    fun onUserSelected(user: UserDomain) {
        model.sendToUser = user
        _userInfo.value = getUserInfo(user)
    }

    private fun getUserInfo(user: UserDomain?) =
        UserInfo(
            name = user?.userName ?: appContext.getString(R.string.select_user),
            avatar = user?.userAvatar,
            isFound = user != null
        )
}