package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R

class MyCommunitiesFragment : Fragment() {
    companion object {
        fun newInstance() = MyCommunitiesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_communities, container, false)
}
