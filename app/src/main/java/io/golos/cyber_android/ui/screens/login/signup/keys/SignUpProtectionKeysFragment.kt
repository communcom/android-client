package io.golos.cyber_android.ui.screens.login.signup.keys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*

class SignUpProtectionKeysFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpProtectionKeysFragment()
    }

    private lateinit var viewModel: SignUpProtectionKeysViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_protection_keys, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        keysList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun observeViewModel() {
        viewModel.getKeysLiveData.observe(this, Observer {
            keysList.adapter = KeysAdapter(it)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(SignUpProtectionKeysViewModel::class.java)
    }

}
