package io.golos.cyber_android.ui.screens.wallet_send_points.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToHomeBackCommand
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToWalletBackCommand
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.*
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.capitalize
import kotlinx.coroutines.launch
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

    val amountInputField = MutableLiveData<String>()

    private val _sendButtonInfo = MutableLiveData<SendButtonInfo>(getSendButtonInfo(amountInputField.value))
    val sendButtonInfo: LiveData<SendButtonInfo> = _sendButtonInfo

    init {
        amountInputField.observeForever {
            if(model.updateAmount(it)) {
                _sendButtonInfo.value = getSendButtonInfo(it)
            } else {
                amountInputField.value = ""
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
        amountInputField.value = ""
    }

    fun onSelectCommunityClick() {
        _command.value = ShowSelectCommunityDialogCommand(model.balance)
    }

    fun onUserSelected(user: UserDomain) {
        model.sendToUser = user
        _selectedUser.value = getUserInfo(user)
        _sendButtonInfo.value = getSendButtonInfo(amountInputField.value)
    }

    fun onCommunitySelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                amountInputField.value = ""
                _selectedBalanceRecord.value = model.currentBalanceRecord
                _amountFieldInfo.value = getAmountFieldInfo()
                _command.value = UpdateCarouselPositionCommand(it)
            }
    }

    fun onCarouselItemSelected(communityId: String) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                amountInputField.value = ""
                _selectedBalanceRecord.value = model.currentBalanceRecord
                _amountFieldInfo.value = getAmountFieldInfo()
            }
    }

    fun onSendClick() {
        launch {
            _command.value = HideKeyboardCommand()

            val validationResult = model.validateAmount()

            if(validationResult == AmountValidationResult.SUCCESS) {
                _command.value = SetLoadingVisibilityCommand(true)
                try {
                    model.makeTransfer()
                    _command.value = ShowWalletTransferCompletedDialogCommand(model.getTransferCompletedInfo())
                } catch(ex: Exception) {
                    _command.value = ShowMessageResCommand(R.string.common_general_error)
                } finally {
                    _command.value = SetLoadingVisibilityCommand(false)
                }
            } else {
                showAmountValidationResult(validationResult)
            }
        }
    }

    fun onBackToWalletSelected() {
        _command.value = NavigateToWalletBackCommand()
    }

    fun onBackToHomeSelected() {
        _command.value = NavigateToHomeBackCommand()
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

            val isEnabled = model.validateAmount() == AmountValidationResult.SUCCESS && model.sendToUser != null

            val sendText = appContext.resources.getFormattedString(R.string.wallet_send_format, amount, name)

            return SendButtonInfo(showFee = model.hasFee, isEnabled = isEnabled, sendText = sendText)
        }
    }

    private fun showAmountValidationResult(validationResult: AmountValidationResult) {
        val resourceId = when(validationResult) {
            AmountValidationResult.TOO_LARGE -> R.string.wallet_amount_validation_too_large
            AmountValidationResult.CANT_BE_ZERO -> R.string.wallet_amount_validation_zero
            AmountValidationResult.INVALID_VALUE -> R.string.wallet_amount_validation_invalid_value
            else -> throw UnsupportedOperationException("This value is not supported: $validationResult")
        }
        _command.value = ShowMessageResCommand(resourceId)
    }
}