package io.golos.cyber_android.ui.screens.profile_photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.PhotoPlace

class ProfilePhotosFragment: Fragment() {
    companion object {
        private const val PLACE = "PLACE"

        fun newInstance(place: PhotoPlace): ProfilePhotosFragment {
            return ProfilePhotosFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLACE, place.value)
                }
            }
        }

    }

    private val place: PhotoPlace
        get() = PhotoPlace.create(arguments!!.getInt(PLACE))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_photos, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}