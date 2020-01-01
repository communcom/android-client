package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.shared.utils.setDrawableToEnd
import kotlinx.android.synthetic.main.dialog_profile_menu.*

class ProfileMenuDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 1548

        const val RESULT_SELECT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 3

        const val ITEM = "ITEM"

        fun newInstance(place: ProfileItem, target: Fragment): ProfileMenuDialog {
            return ProfileMenuDialog().apply {
                arguments = Bundle().apply {
                    putInt(ITEM, place.value)
                }
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_profile_menu

    override fun setupView() {
        val place = ProfileItem.create(arguments!!.getInt(ITEM))

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
        }

        select.setSelectAction(RESULT_SELECT) {
            putExtra(ITEM, place.value)
        }

        delete.setSelectAction(RESULT_DELETE) {
            putExtra(ITEM, place.value)
        }

        closeButton.setSelectAction(RESULT_CANCEL) {
            putExtra(ITEM, place.value)
        }
    }
}