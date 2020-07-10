package io.golos.cyber_android.ui.screens.wallet_convert.view_model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToHomeBackCommand
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToWalletBackCommand
import io.golos.cyber_android.ui.screens.wallet_convert.dto.*
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModel
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectCommunityDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UpdateCarouselPositionCommand
import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import io.golos.cyber_android.ui.screens.wallet_shared.getDisplayName
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.format.CurrencyFormatter
import kotlinx.coroutines.launch
import javax.inject.Inject

class WalletConvertViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletConvertModel
) : ViewModelBase<WalletConvertModel>(dispatchersProvider, model) {

    companion object {
        val BALANCE_UPDATED_EVENT = "balance_updated"
    }

    private val _sellerBalanceRecord = MutableLiveData<WalletCommunityBalanceRecordDomain>()
    val sellerBalanceRecord: LiveData<WalletCommunityBalanceRecordDomain> = _sellerBalanceRecord

    private val _carouselItems = MutableLiveData<CarouselStartData>()
    val carouselItems: LiveData<CarouselStartData> = _carouselItems

    val title: Int = R.string.convert

    private val _isInCarouselMode = MutableLiveData<Boolean>()
    val isInCarouselMode: LiveData<Boolean> = _isInCarouselMode

    private val _inputFieldInfo = MutableLiveData<InputFieldsInfo>()
    val inputFieldInfo: LiveData<InputFieldsInfo> = _inputFieldInfo

    private val _sellInputField = MutableLiveData<String>()
    val sellInputField: LiveData<String> = _sellInputField

    private val _buyInputField = MutableLiveData<String>()
    val buyInputField: LiveData<String> = _buyInputField

    private val _convertButtonInfo = MutableLiveData<ConvertButtonInfo>()
    val convertButtonInfo: LiveData<ConvertButtonInfo> = _convertButtonInfo

    private val _errorLabelInfo = MutableLiveData<ErrorLabelInfo>()
    val errorLabelInfo: LiveData<ErrorLabelInfo> = _errorLabelInfo

    private val _pointInfo = MutableLiveData<PointInfo>()
    val pointInfo: LiveData<PointInfo> = _pointInfo

    private val _isMenuVisible = MutableLiveData<Boolean>()
    val isMenuVisible: LiveData<Boolean> = _isMenuVisible

    init {
        launch {
            model.init()

            _sellerBalanceRecord.value = getSellerRecord()
            _carouselItems.value =model.carouselItemsData
            _isInCarouselMode.value = model.isInSellPointMode
            _inputFieldInfo.value = getInputFieldsInfo()
            _sellInputField.value = getSellAmount()
            _buyInputField.value = getBuyAmount()
            _convertButtonInfo.value = getConvertButtonInfo()
            _errorLabelInfo.value = getErrorLabelInfo()
            _pointInfo.value = getPointInfo()
            _isMenuVisible.value = model.isInSellPointMode
        }
    }

    fun onSellInputFieldUpdated(value: String) {
        if(!model.amountCalculator.updateSellAmount(value)) {
            _sellInputField.value = getSellAmount()
        }

        _buyInputField.value = getBuyAmount()

        _convertButtonInfo.value = getConvertButtonInfo()
        _errorLabelInfo.value = getErrorLabelInfo()
    }

    fun onBuyInputFieldUpdated(value: String) {
        if(!model.amountCalculator.updateBuyAmount(value)) {
            _buyInputField.value = getBuyAmount()
        }

        _sellInputField.value = getSellAmount()
        _convertButtonInfo.value = getConvertButtonInfo()
        _errorLabelInfo.value = getErrorLabelInfo()
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSelectCommunityClick() {
        _command.value = ShowSelectCommunityDialogCommand(model.balance)
    }

    fun onCommunitySelected(communityId: CommunityIdDomain) = updateCurrentCommunity(communityId, updateCarousel = true)

    fun onCarouselItemSelected(communityId: CommunityIdDomain) = updateCurrentCommunity(communityId, updateCarousel = false)

    fun onSwapClick() {
        model.swipeSellMode()

        _sellerBalanceRecord.value = getSellerRecord()
        _isInCarouselMode.value = model.isInSellPointMode
        _inputFieldInfo.value = getInputFieldsInfo()

        _sellInputField.value = getSellAmount()
        _buyInputField.value = getBuyAmount()

        _convertButtonInfo.value = getConvertButtonInfo()
        _errorLabelInfo.value = getErrorLabelInfo()
        _pointInfo.value = getPointInfo()
        _isMenuVisible.value = model.isInSellPointMode
    }

    fun onClearInputField() {
        model.amountCalculator.updateSellAmount("")
        _sellInputField.value = getSellAmount()
        _buyInputField.value = getBuyAmount()
        _convertButtonInfo.value = getConvertButtonInfo()
        _errorLabelInfo.value = getErrorLabelInfo()
    }

    fun onSendButtonClickListener() {
        launch {
            _command.value = HideKeyboardCommand()

            val validationResult = model.validateAmount()

            if(validationResult.isValid) {
                _command.value = SetLoadingVisibilityCommand(true)
                try {
                    model.convert()
                    sendBalanceUpdateEvent()
                    _command.value = ShowWalletConversionCompletedDialogCommand(model.getConversionCompletedInfo())
                } catch(ex: Exception) {
                    _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
                } finally {
                    _command.value = SetLoadingVisibilityCommand(false)
                }
            } else {
                showAmountValidationResult(validationResult)
            }
        }
    }

    private fun sendBalanceUpdateEvent() {
        val intent = Intent(BALANCE_UPDATED_EVENT)
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent)
    }

    fun onBackToWalletSelected() {
        _command.value = NavigateToWalletBackCommand()
    }

    fun onBackToHomeSelected() {
        _command.value = NavigateToHomeBackCommand()
    }

    private fun updateCurrentCommunity(communityId: CommunityIdDomain, updateCarousel: Boolean) {
        launch {
            model.updateCurrentCommunity(communityId)
                ?.let {
                    _sellInputField.value = getSellAmount()
                    _buyInputField.value = getBuyAmount()

                    _sellerBalanceRecord.value = getSellerRecord()

                    _inputFieldInfo.value = getInputFieldsInfo()
                    _convertButtonInfo.value = getConvertButtonInfo()
                    _errorLabelInfo.value = getErrorLabelInfo()

                    _pointInfo.value = getPointInfo()

                    if(updateCarousel) {
                        _command.value = UpdateCarouselPositionCommand(it)
                    }
                }
        }
    }

    private fun getInputFieldsInfo() =
        InputFieldsInfo(
            buyerHint = R.string.digit0,
            sellerHint = R.string.digit0,
            buyerDecimalPoints = if(model.isInSellPointMode) 4 else 3,
            sellerDecimalPoints = if(model.isInSellPointMode) 3 else 4
        )

    private fun getPointInfo(): PointInfo {
        val seller = getSellerRecord()
        val buyer = getBuyerRecord()

        val rateText = if(model.isInSellPointMode) {
            appContext.resources.getFormattedString(
                R.string.wallet_convert_rate_format,
                model.amountCalculator.pointsInCommun,
                seller.communityName!!,
                1,
                buyer.getDisplayName(appContext))
        } else {
            appContext.resources.getFormattedString(
                R.string.wallet_convert_rate_format,
                1,
                seller.communityName!!,
                model.amountCalculator.pointsInCommun,
                buyer.communityName!!)
        }

        return PointInfo(
            sellerName = seller.getDisplayName(appContext),
            buyerName = buyer.getDisplayName(appContext),
            buyerLogoUrl = buyer.communityLogoUrl,
            buyerBalance = buyer.points,
            canSelectPoint = !model.isInSellPointMode,
            rateInfo = rateText
        )
    }

    private fun getConvertButtonInfo(): ConvertButtonInfo {
        val seller = getSellerRecord()
        val sellValue = model.amountCalculator.sellAmount?.let { amount -> CurrencyFormatter.format(amount)} ?: "0"
        return ConvertButtonInfo (
            text = appContext.resources.getFormattedString(
                R.string.wallet_convert_convert_format,
                sellValue,
                seller.communityName!!),
            isEnabled = model.validateAmount().isValid
        )
    }

    private fun getErrorLabelInfo(): ErrorLabelInfo =
        with(model.validateAmount()) {
            when {
                buyAmount == AmountValidationResult.TOO_LARGE || sellAmount == AmountValidationResult.TOO_LARGE ->
                    ErrorLabelInfo(true, appContext.getString(R.string.wallet_amount_validation_buy_too_large))

                else -> ErrorLabelInfo(false, null)
            }
        }

    private fun getSellerRecord() = model.getSellerRecord().let { it.copy(communityName = it.getDisplayName(appContext)) }

    private fun getBuyerRecord() = model.getBuyerRecord().let { it.copy(communityName = it.getDisplayName(appContext)) }

    private fun getSellAmount() = amountToString(model.amountCalculator.sellAmount)

    private fun getBuyAmount() = amountToString(model.amountCalculator.buyAmount)

    private fun amountToString(value: Double?): String = value?.toString() ?: ""

    private fun showAmountValidationResult(validationResult: ConvertAmountValidationResult) {
        val resourceId = when(validationResult.buyAmount) {
            AmountValidationResult.TOO_LARGE -> R.string.wallet_amount_validation_buy_too_large
            AmountValidationResult.CANT_BE_ZERO -> R.string.wallet_amount_validation_buy_zero
            AmountValidationResult.INVALID_VALUE -> R.string.wallet_amount_validation_buy_invalid_value
            else -> {
                when(validationResult.sellAmount) {
                    AmountValidationResult.TOO_LARGE -> R.string.wallet_amount_validation_sell_too_large
                    AmountValidationResult.CANT_BE_ZERO -> R.string.wallet_amount_validation_sell_zero
                    AmountValidationResult.INVALID_VALUE -> R.string.wallet_amount_validation_sell_invalid_value
                    else -> throw UnsupportedOperationException("This value is not supported: $validationResult")
                }
            }
        }
        _command.value = ShowMessageResCommand(resourceId)
    }
}