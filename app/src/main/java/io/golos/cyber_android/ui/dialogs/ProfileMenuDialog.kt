package io.golos.cyber_android.ui.dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.shared.utils.setDrawableToEnd
import kotlinx.android.synthetic.main.dialog_profile_menu.*

class ProfileMenuDialog(private val place: ProfileItem) : BottomSheetDialogFragmentBase<ProfileMenuDialog.Result>() {
    sealed class Result {
        data class Select (val place: ProfileItem): Result()
        data class Delete (val place: ProfileItem): Result()
    }

    companion object {
        fun show(parent: Fragment, place: ProfileItem, closeAction: (Result?) -> Unit) =
            ProfileMenuDialog(place)
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "PROFILE_MENU_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_profile_menu


    override fun setupView() {
        when(place) {
            ProfileItem.COVER -> {
                title.text = context!!.resources.getString(R.string.change_profile_cover)
                select.text = context!!.resources.getString(R.string.select_cover)
                delete.text = context!!.resources.getString(R.string.delete_current_cover)

                select.setDrawableToEnd(R.drawable.ic_photo)
            }
            ProfileItem.AVATAR -> {
                title.text = context!!.resources.getString(R.string.change_profile_photo)
                select.text = context!!.resources.getString(R.string.select_photo)
                delete.text = context!!.resources.getString(R.string.delete_current_photo)

                select.setDrawableToEnd(R.drawable.ic_photo)
            }
            ProfileItem.BIO -> {
                title.text = context!!.resources.getString(R.string.change_profile_description)
                select.text = context!!.resources.getString(R.string.edit_description)
                delete.text = context!!.resources.getString(R.string.delete_description)

                select.setDrawableToEnd(R.drawable.ic_edit)
            }

            else -> {}
        }

        select.setOnClickListener { closeOnItemSelected(Result.Select(place)) }
        delete.setOnClickListener { closeOnItemSelected(Result.Delete(place)) }
    }
}