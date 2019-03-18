package io.golos.cyber_android.ui.screens.communities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.golos.cyber_android.R

class CommunitiesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communities, container, false)
    }

    companion object {
        fun newInstance() = CommunitiesFragment()
    }
}
