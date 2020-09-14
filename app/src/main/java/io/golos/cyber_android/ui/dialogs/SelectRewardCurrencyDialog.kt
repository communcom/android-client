package io.golos.cyber_android.ui.dialogs

import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.RewardCurrency
import io.golos.cyber_android.ui.shared.utils.openLinkView
import kotlinx.android.synthetic.main.dialog_select_reward_currency.*
import android.content.Intent

class SelectRewardCurrencyDialog(
    private val startCurrency: RewardCurrency
) : BottomSheetDialogFragmentBase<SelectRewardCurrencyDialog.Result>() {

    data class Result(
        val rewardCurrency: RewardCurrency
    )

    private var selectedCurrency = startCurrency
    private val browserPackageName = "com.android.chrome"

    companion object {
        fun show(parent: Fragment, startCurrency: RewardCurrency, closeAction: (Result?) -> Unit) =
            SelectRewardCurrencyDialog(startCurrency)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "SELECT_REWARD_CURRENCY_DIALOG")
    }

    override val closeButton: View?
        get() = null

    override val layout: Int
        get() = R.layout.dialog_select_reward_currency

    override fun setupView() {
        showCurrency()

        rewardCurrencyLabel.setOnClickListener { rewardCurrencyButton.performClick() }
        rewardCurrencyButton.setOnClickListener { switchCurrency() }

        learnMoreButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.POINTS_URI))
            //ToDo this is a temporary solution, needs to be changed
            intent.setPackage(browserPackageName)
            startActivity(intent)
        }
    }

    override fun processDestroy() {
        if(startCurrency == selectedCurrency) {
            closeActionListener(null)
        } else {
            closeActionListener(Result(selectedCurrency))
        }
    }

    private fun showCurrency() {
        val resId = when(selectedCurrency) {
            RewardCurrency.COMMUNS -> R.string.commun_currency
            RewardCurrency.POINTS -> R.string.points_currency
            RewardCurrency.USD -> R.string.usd_currency
        }

        rewardCurrencyLabel.setText(resId)
    }

    private fun switchCurrency() {
        selectedCurrency = when(selectedCurrency) {
            RewardCurrency.COMMUNS -> RewardCurrency.POINTS
            RewardCurrency.POINTS -> RewardCurrency.USD
            RewardCurrency.USD -> RewardCurrency.COMMUNS
        }

        showCurrency()
    }
}