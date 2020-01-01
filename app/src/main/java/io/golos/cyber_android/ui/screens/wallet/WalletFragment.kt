package io.golos.cyber_android.ui.screens.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.FragmentBase

class WalletFragment : FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    companion object {
        fun newInstance() = WalletFragment()
    }

}
