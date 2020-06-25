package io.golos.cyber_android.ui.dialogs.donation

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.dialog_donations_users.*

class DonationUsersDialog(private val donation: DonationsDomain) : BottomSheetDialogFragmentBase<DonationUsersDialog.Result>() {
    sealed class Result {
        data class ItemSelected (val user: UserIdDomain): Result()
    }

    companion object {
        fun show(parent: Fragment, donation: DonationsDomain, closeAction: (Result?) -> Unit) =
            DonationUsersDialog(donation)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "DONATION_USERS_DIALOG")
    }

    override val closeButton: View?
        get() = ivClose

    override val layout: Int
        get() = R.layout.dialog_donations_users


    override fun setupView() {
        val onClickAction: (UserIdDomain) -> Unit = { closeOnItemSelected(Result.ItemSelected(it)) }

        for(i in donation.donators.indices) {
            val userView = DonationUsersDialogItem(requireContext())
            userView.init(donation.contentId, donation.donators[i], i == donation.donators.lastIndex, onClickAction)
            itemsContainer.addView(userView)
        }
    }
}