package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.PhotoPlace
import kotlinx.android.synthetic.main.dialog_profile_photo_menu.*

class ProfilePhotoMenuDialog : BottomSheetDialogFragmentBase() {
    companion object {
        const val REQUEST = 1548

        const val RESULT_SELECT = Activity.RESULT_FIRST_USER + 1
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 2
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 3

        const val PLACE = "PLACE"

        fun newInstance(place: PhotoPlace, target: Fragment): ProfilePhotoMenuDialog {
            return ProfilePhotoMenuDialog().apply {
                arguments = Bundle().apply {
                    putInt(PLACE, place.value)
                }
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_profile_photo_menu

    override fun setupView() {
        val place = PhotoPlace.create(arguments!!.getInt(PLACE))

        when(place) {
            PhotoPlace.COVER -> {
                title.text = context!!.resources.getString(R.string.change_profile_cover)
                delete.text = context!!.resources.getString(R.string.delete_current_cover)
            }

            PhotoPlace.AVATAR -> {
                title.text = context!!.resources.getString(R.string.change_profile_photo)
                delete.text = context!!.resources.getString(R.string.delete_current_photo)
            }
        }

        select.setSelectAction(RESULT_SELECT) {
            putExtra(PLACE, place.value)
        }

        delete.setSelectAction(RESULT_DELETE) {
            putExtra(PLACE, place.value)
        }

        closeButton.setSelectAction(RESULT_CANCEL) {
            putExtra(PLACE, place.value)
        }
    }
}