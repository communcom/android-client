package io.golos.cyber_android.ui.screens.profile_bio.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileBioBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.profile_bio.dto.PrepareToCloseCommand
import io.golos.cyber_android.ui.screens.profile_bio.dto.PassResultCommand
import io.golos.cyber_android.ui.screens.profile_bio.view_model.ProfileBioViewModel
import kotlinx.android.synthetic.main.fragment_profile_bio.*

class ProfileBioFragment : FragmentBaseMVVM<FragmentProfileBioBinding, ProfileBioViewModel>() {
    companion object {
        private const val TEXT = "TEXT"

        const val RESULT = "RESULT"
        const val REQUEST = 4504

        fun newInstance(text: String?, parentFragment: Fragment) =
            ProfileBioFragment().apply {
                    setTargetFragment(parentFragment, REQUEST)
                    arguments = Bundle().apply {
                        putString(TEXT, text)
                    }
                }
    }

    override fun provideViewModelType(): Class<ProfileBioViewModel> = ProfileBioViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_bio

    override fun inject() = App.injections.get<ProfileBioFragmentComponent>(arguments!!.getString(TEXT)).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileBioFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileBioBinding, viewModel: ProfileBioViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is BackCommand -> requireActivity().onBackPressed()
            is PassResultCommand -> passResult(command.text)
            is PrepareToCloseCommand -> prepareToClose()
        }
    }

    private fun passResult(text: String) {
        targetFragment!!.onActivityResult(
            REQUEST,
            Activity.RESULT_OK,
            Intent().apply {
                this.putExtra(RESULT, text)
            })
    }

    private fun prepareToClose() {
        uiHelper.setSoftKeyboardVisibility(editor, false)
        editor.clearFocus()
    }
}