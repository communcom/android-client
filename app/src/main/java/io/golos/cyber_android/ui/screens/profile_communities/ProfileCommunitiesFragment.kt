package io.golos.cyber_android.ui.screens.profile_communities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R

class ProfileCommunitiesFragment: Fragment() {
    companion object {
        fun newInstance() = ProfileCommunitiesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_communities, container, false)
    }
}