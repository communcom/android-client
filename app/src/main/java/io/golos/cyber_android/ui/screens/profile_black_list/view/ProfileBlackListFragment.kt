package io.golos.cyber_android.ui.screens.profile_black_list.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileBlackListBinding
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_black_list.view_model.ProfileBlackListViewModel
import io.golos.domain.GlobalConstants
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

    override fun inject(key: String) =
        App.injections
            .get<ProfileBlackListFragmentComponent>(
                key,
                BlackListFilter.create(arguments!!.getInt(FILTER)),
                GlobalConstants.PAGE_SIZE)
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileBlackListFragmentComponent>(key)

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
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
        }
    }
}
