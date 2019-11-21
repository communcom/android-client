package io.golos.cyber_android.ui.dialogs

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.dialog_profile_photo_menu.*

class ProfilePhotoMenuDialog : BottomSheetDialogFragmentBase() {
    enum class Type(val value: Int) {
        COVER(0),
        AVATAR(1)
    }

    companion object {
        const val REQUEST = 1548

        const val RESULT_GALLERY = Activity.RESULT_FIRST_USER + 1
        const val RESULT_CAMERA = Activity.RESULT_FIRST_USER + 2
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 3
        const val RESULT_CANCEL = Activity.RESULT_FIRST_USER + 4

        const val TYPE = "TYPE"

        fun newInstance(type: Type, target: Fragment): ProfilePhotoMenuDialog {
            return ProfilePhotoMenuDialog().apply {
                arguments = Bundle().apply {
                    putInt(TYPE, type.value)
                }
                setTargetFragment(target, REQUEST)
            }
        }
    }

    override fun provideLayout(): Int = R.layout.dialog_profile_photo_menu

    override fun setupView() {
        val type = Type.values().first { it.value == arguments!!.getInt(TYPE) }

        when(type) {
            Type.COVER -> {
                title.text = context!!.resources.getString(R.string.change_profile_cover)
                delete.text = context!!.resources.getString(R.string.delete_current_cover)
            }

            Type.AVATAR -> {
                title.text = context!!.resources.getString(R.string.change_profile_photo)
                delete.text = context!!.resources.getString(R.string.delete_current_photo)
            }
        }

        gallery.setSelectAction(RESULT_GALLERY) {
            putExtra(TYPE, type)
        }

        camera.setSelectAction(RESULT_CAMERA) {
            putExtra(TYPE, type)
        }

        delete.setSelectAction(RESULT_DELETE) {
            putExtra(TYPE, type)
        }

        closeButton.setSelectAction(RESULT_CANCEL) {
            putExtra(TYPE, type)
        }
    }
}