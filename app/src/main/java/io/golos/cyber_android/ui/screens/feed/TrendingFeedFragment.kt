package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R

/**
 * Fragment that represents TRENDING tab of the Feed Page.
 */
class TrendingFeedFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    companion object {
        fun newInstance() = TrendingFeedFragment()
    }
}