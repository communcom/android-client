package io.golos.cyber_android.ui.screens.wallet_send_points.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.*
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.capitalize
import java.util.*
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

    private val _amountFieldInfo = MutableLiveData<AmountFieldInfo>(getAmountFieldInfo())
    val amountFieldInfo: LiveData<AmountFieldInfo> = _amountFieldInfo

    val amountAsString = MutableLiveData<String>()

    private val _sendButtonInfo = MutableLiveData<SendButtonInfo>(getSendButtonInfo(amountAsString.value))
    val sendButtonInfo: LiveData<SendButtonInfo> = _sendButtonInfo

    init {
        amountAsString.observeForever {
            if(model.updateAmount(it)) {
                _sendButtonInfo.value = getSendButtonInfo(it)
            } else {
                amountAsString.value = ""
            }
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSelectUserClick() {
        _command.value = ShowSelectUserDialogCommand()
    }

    fun onClearAmountClick() {
        amountAsString.value = ""
    }

    fun onSelectCommunityClick() {
        _command.value = ShowSelectCommunityDialogCommand(model.balance)
    }

    fun onUserSelected(user: UserDomain) {
        model.sendToUser = user
        _selectedUser.value = getUserInfo(user)
        _sendButtonInfo.value = getSendButtonInfo(amountAsString.value)
    }

    fun onCommunitySelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                amountAsString.value = ""
                _selectedBalanceRecord.value = model.currentBalanceRecord
                _amountFieldInfo.value = getAmountFieldInfo()
                _command.value = UpdateCarouselPositionCommand(it)
            }
    }

    fun onCarouselItemSelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                amountAsString.value = ""
                _selectedBalanceRecord.value = model.currentBalanceRecord
                _amountFieldInfo.value = getAmountFieldInfo()
            }
    }

    fun onSendClick() {
        _command.value = HideKeyboardCommand()
    }

    private fun getUserInfo(user: UserDomain?) =
        UserInfo(
            name = user?.userName ?: appContext.getString(R.string.select_user),
            avatar = user?.userAvatar,
            isFound = user != null
        )

    private fun getAmountFieldInfo() =
        if(model.currentBalanceRecord.communityId != GlobalConstants.COMMUN_CODE) {
            AmountFieldInfo(hintResId = R.string.points_0, decimalPointsQuantity = 3)
        } else {
            AmountFieldInfo(hintResId = R.string.commun_0, decimalPointsQuantity = 4)
        }

    private fun getSendButtonInfo(amountString: String?): SendButtonInfo {
        with(model.currentBalanceRecord) {
            val amount = if(amountString.isNullOrBlank()) "0" else amountString

            val name = if(communityId != GlobalConstants.COMMUN_CODE) {
                communityName ?: communityId
            } else {
                appContext.getString(R.string.commun).capitalize(Locale.getDefault())
            }

            val showFee = model.currentBalanceRecord.communityId != GlobalConstants.COMMUN_CODE

            val isEnabled = !amountString.isNullOrBlank() && model.sendToUser != null

            val sendText = appContext.resources.getFormattedString(R.string.wallet_send_format, amount, name)

            return SendButtonInfo(showFee = showFee, isEnabled = isEnabled, sendText = sendText)
        }
    }
}