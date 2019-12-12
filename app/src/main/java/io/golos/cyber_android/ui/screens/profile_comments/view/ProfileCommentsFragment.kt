package io.golos.cyber_android.ui.screens.profile_comments.view

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileCommentsBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsViewModel

class ProfileCommentsFragment : FragmentBaseMVVM<FragmentProfileCommentsBinding, ProfileCommentsViewModel>() {

    override fun provideViewModelType(): Class<ProfileCommentsViewModel> = ProfileCommentsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_comments

    override fun inject() = App.injections.get<ProfileCommentsFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommentsFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileCommentsBinding, viewModel: ProfileCommentsViewModel) {
        binding.viewModel = viewModel
    }

    companion object {
        fun newInstance() = ProfileCommentsFragment()
    }
}