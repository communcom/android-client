package io.golos.cyber_android.ui.screens.login_sign_up.fragments.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.base.FragmentBase
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import kotlinx.android.synthetic.main.fragment_sign_up_key.*

class SignUpKeyFragment : FragmentBase() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_key, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moveToPasscodeButton.setOnClickListener {
            navigateToProtectionStep()
        }
    }

    private fun navigateToProtectionStep() {
        findNavController().safeNavigate(R.id.signUpKeyFragment, R.id.action_signUpKeyFragment_to_pinCodeFragment)
    }
}
