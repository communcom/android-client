package io.golos.cyber_android.ui.screens.wallet_convert.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ConvertButtonInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.InputFieldsInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.PointInfo
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModel
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectCommunityDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UpdateCarouselPositionCommand
import io.golos.cyber_android.ui.screens.wallet_shared.getDisplayName
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject

class WalletConvertViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletConvertModel
) : ViewModelBase<WalletConvertModel>(dispatchersProvider, model) {

    private val _sellerBalanceRecord = MutableLiveData<WalletCommunityBalanceRecordDomain>(getSellerRecord())
    val sellerBalanceRecord: LiveData<WalletCommunityBalanceRecordDomain> = _sellerBalanceRecord

    private val _carouselItems = MutableLiveData<CarouselStartData>(model.carouselItemsData)
    val carouselItems: LiveData<CarouselStartData> = _carouselItems

    val title: Int = R.string.convert

    private val _isInCarouselMode = MutableLiveData<Boolean>(model.isInSellPointMode)
    val isInCarouselMode: LiveData<Boolean> = _isInCarouselMode

    private val _inputFieldInfo = MutableLiveData<InputFieldsInfo>(getInputFieldsInfo())
    val inputFieldInfo: LiveData<InputFieldsInfo> = _inputFieldInfo

    private val _sellInputField = MutableLiveData<String>(getSellAmount())
    val sellInputField: LiveData<String> = _sellInputField

    private val _buyInputField = MutableLiveData<String>(getBuyAmount())
    val buyInputField: LiveData<String> = _buyInputField

    private val _convertButtonInfo = MutableLiveData<ConvertButtonInfo>(getConvertButtonInfo())
    val convertButtonInfo: LiveData<ConvertButtonInfo> = _convertButtonInfo

    private val _pointInfo = MutableLiveData<PointInfo>(getPointInfo())
    val pointInfo: LiveData<PointInfo> = _pointInfo

    private val _isMenuVisible = MutableLiveData<Boolean>(model.isInSellPointMode)
    val isMenuVisible: LiveData<Boolean> = _isMenuVisible

    fun onSellInputFieldUpdated(value: String) {
        if(!model.amountCalculator.updateSellAmount(value)) {
            _sellInputField.value = getSellAmount()
        }

        _buyInputField.value = getBuyAmount()
        _convertButtonInfo.value = getConvertButtonInfo()
    }

    fun onBuyInputFieldUpdated(value: String) {
        if(!model.amountCalculator.updateBuyAmount(value)) {
            _buyInputField.value = getBuyAmount()
        }

        _sellInputField.value = getSellAmount()
        _convertButtonInfo.value = getConvertButtonInfo()
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSelectCommunityClick() {
        _command.value = ShowSelectCommunityDialogCommand(model.balance)
    }

    fun onCommunitySelected(communityId: String) = updateCurrentCommunity(communityId, updateCarousel = true)

    fun onCarouselItemSelected(communityId: String) = updateCurrentCommunity(communityId, updateCarousel = false)

    fun onSwapClick() {
        model.swipeSellMode()

        _sellerBalanceRecord.value = getSellerRecord()
        _isInCarouselMode.value = model.isInSellPointMode
        _inputFieldInfo.value = getInputFieldsInfo()

        _sellInputField.value = getSellAmount()
        _buyInputField.value = getBuyAmount()

        _convertButtonInfo.value = getConvertButtonInfo()
        _pointInfo.value = getPointInfo()
        _isMenuVisible.value = model.isInSellPointMode
    }

    fun onClearInputField() {
        model.amountCalculator.updateSellAmount("")
        _sellInputField.value = getSellAmount()
        _buyInputField.value = getBuyAmount()
        _convertButtonInfo.value = getConvertButtonInfo()
    }

    private fun updateCurrentCommunity(communityId: String, updateCarousel: Boolean) {
        model.updateCurrentCommunity(communityId)
            ?.let {
                _sellInputField.value = getSellAmount()
                _buyInputField.value = getBuyAmount()

                _sellerBalanceRecord.value = getSellerRecord()

                _inputFieldInfo.value = getInputFieldsInfo()
                _convertButtonInfo.value = getConvertButtonInfo()

                _pointInfo.value = getPointInfo()

                if(updateCarousel) {
                    _command.value = UpdateCarouselPositionCommand(it)
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
            buttonText = appContext.resources.getFormattedString(
                R.string.wallet_convert_convert_format,
                sellValue,
                seller.communityName!!),
            isButtonEnabled = true
        )
    }

    private fun getSellerRecord() = model.getSellerRecord().let { it.copy(communityName = it.getDisplayName(appContext)) }

    private fun getBuyerRecord() = model.getBuyerRecord().let { it.copy(communityName = it.getDisplayName(appContext)) }

    private fun getSellAmount() = amountToString(model.amountCalculator.sellAmount)

    private fun getBuyAmount() = amountToString( model.amountCalculator.buyAmount)

    private fun amountToString(value: Double?): String = value?.toString() ?: ""
}