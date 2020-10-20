package io.golos.cyber_android.ui.screens.profile_liked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.fragment_profile_liked.*

class ProfileLikedFragment : Fragment() {
    companion object {
        fun newInstance() = ProfileLikedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_liked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener { requireActivity().onBackPressed() }
    }
}