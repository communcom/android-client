package io.golos.cyber_android.ui.screens.profile_posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R

class ProfilePostsFragment: Fragment() {
    companion object {
        fun newInstance() = ProfilePostsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_posts, container, false)
    }
}