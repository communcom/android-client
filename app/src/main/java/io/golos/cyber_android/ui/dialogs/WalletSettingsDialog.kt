package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_profile_settings.buttonClose
import kotlinx.android.synthetic.main.dialog_wallet_settings.*


class WalletSettingsDialog(private val emptyBalanceVisibility: Boolean): BottomSheetDialogFragmentBase<WalletSettingsDialog.Result>() {
    sealed class Result {
        data class StateSelected (val emptyBalanceVisibility: Boolean): WalletSettingsDialog.Result()
    }

    companion object {
        fun show(parent: Fragment, emptyBalanceVisibility: Boolean, closeAction: (Result?) -> Unit) =
            WalletSettingsDialog(emptyBalanceVisibility)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "WALLET_SETTINGS_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_wallet_settings

    override fun setupView() {
        if(emptyBalanceVisibility)
            emptyBalancesShowHide.isChecked = emptyBalanceVisibility
        emptyBalancesShowHide.setOnCheckedChangeListener { _, isChecked ->
            closeOnItemSelected(Result.StateSelected(isChecked))
        }
    }
}