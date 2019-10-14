package io.golos.cyber_android.ui.screens.followers

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers.FollowersFragmentComponent
import io.golos.cyber_android.databinding.FragmentFollowersBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import kotlinx.android.synthetic.main.item_toolbar.*

class FollowersFragment : FragmentBaseMVVM<FragmentFollowersBinding, FollowersModel, FollowersViewModel>() {

    override fun provideViewModelType(): Class<FollowersViewModel> = FollowersViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_followers

    override fun inject() = App.injections
        .get<FollowersFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentFollowersBinding, viewModel: FollowersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbarTitle.setText(R.string.followers)
        ivBack.setOnClickListener {
            viewModel.back()
        }
    }
}