package io.golos.cyber_android.ui.screens.profile_black_list.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileBlackListBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_black_list.view_model.ProfileBlackListViewModel
import kotlinx.android.synthetic.main.fragment_profile_black_list.*

class ProfileBlackListFragment : FragmentBaseMVVM<FragmentProfileBlackListBinding, ProfileBlackListViewModel>() {
    companion object {
        private const val FILTER = "FILTER"

        fun newInstance(filter: BlackListFilter) = ProfileBlackListFragment().apply {
            arguments = Bundle().apply {
                putInt(FILTER, filter.value)
            }
        }
    }

    override fun provideViewModelType(): Class<ProfileBlackListViewModel> = ProfileBlackListViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_black_list

    override fun inject() =
        App.injections
            .get<ProfileBlackListFragmentComponent>(
                BlackListFilter.create(arguments!!.getInt(FILTER)),
                25)         // Page size
            .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileBlackListFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileBlackListBinding, viewModel: ProfileBlackListViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersList.setAdapterData(viewModel.pageSize, viewModel)
        communitiesList.setAdapterData(viewModel.pageSize, viewModel)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is BackCommand -> requireActivity().onBackPressed()
        }
    }
}
