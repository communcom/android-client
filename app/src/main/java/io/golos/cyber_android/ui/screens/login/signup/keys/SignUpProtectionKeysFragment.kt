package io.golos.cyber_android.ui.screens.login.signup.keys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.login.signup.onboarding.image.OnboardingUserImageFragment
import io.golos.domain.interactors.model.GeneratedUserKeys
import io.golos.sharedmodel.CyberName
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*

class SignUpProtectionKeysFragment : Fragment() {

    data class Args(val user: CyberName, val keys: GeneratedUserKeys)

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

        backup.setOnClickListener {
            requireContext().serviceLocator.backupManager.dataChanged()
        }

        later.setOnClickListener {
            findNavController().safeNavigate(
                R.id.signUpProtectionKeysFragment,
                R.id.action_signUpProtectionKeysFragment_to_onboardingUserImageFragment,
                Bundle().apply {
                    putString(
                        Tags.ARGS,
                        requireContext()
                            .serviceLocator.moshi
                            .adapter(OnboardingUserImageFragment.Args::class.java)
                            .toJson(OnboardingUserImageFragment.Args(getArgs().user))
                    )
                }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.getKeysLiveData.observe(this, Observer {
            keysList.adapter = KeysAdapter(it)
        })
        viewModel.setInitialKeys(getArgs().keys)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(SignUpProtectionKeysViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!
}
