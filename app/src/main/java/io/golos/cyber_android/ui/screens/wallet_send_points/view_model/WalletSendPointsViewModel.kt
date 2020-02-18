package io.golos.cyber_android.ui.screens.wallet_send_points.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectCommunityDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectUserDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UpdateCarouselPositionCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UserInfo
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject

class WalletSendPointsViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletSendPointsModel
) : ViewModelBase<WalletSendPointsModel>(dispatchersProvider, model) {

    private val _selectedUser = MutableLiveData<UserInfo>(getUserInfo(model.sendToUser))
    val selectedUser: LiveData<UserInfo> = _selectedUser

    private val _selectedBalanceRecord = MutableLiveData<WalletCommunityBalanceRecordDomain>(model.currentBalanceRecord)
    val selectedBalanceRecord: LiveData<WalletCommunityBalanceRecordDomain> = _selectedBalanceRecord

    private val _carouselItems = MutableLiveData<CarouselStartData>(model.carouselItemsData)
    val carouselItems: LiveData<CarouselStartData> = _carouselItems

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSelectUserClick() {
        _command.value = ShowSelectUserDialogCommand()
    }

    fun onSelectCommunityClick() {
        _command.value = ShowSelectCommunityDialogCommand(model.balance)
    }

    fun onUserSelected(user: UserDomain) {
        model.sendToUser = user
        _selectedUser.value = getUserInfo(user)
    }

    fun onCommunitySelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                _selectedBalanceRecord.value = model.currentBalanceRecord
                _command.value = UpdateCarouselPositionCommand(it)
            }
    }

    fun onCarouselItemSelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                _selectedBalanceRecord.value = model.currentBalanceRecord
            }
    }

    private fun getUserInfo(user: UserDomain?) =
        UserInfo(
            name = user?.userName ?: appContext.getString(R.string.select_user),
            avatar = user?.userAvatar,
            isFound = user != null
        )
}