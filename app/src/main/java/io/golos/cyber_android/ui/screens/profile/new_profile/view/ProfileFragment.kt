package io.golos.cyber_android.ui.screens.profile.new_profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.ProfileFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileNewBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.extensions.getColorRes
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.widgets.TabLineDrawable
import io.golos.cyber_android.ui.dialogs.ProfilePhotoMenuDialog
import io.golos.cyber_android.ui.screens.profile.new_profile.dto.ShowSelectAvatarDialogCommand
import io.golos.cyber_android.ui.screens.profile.new_profile.dto.ShowSelectCoverDialogCommand
import io.golos.cyber_android.ui.screens.profile.new_profile.view_model.ProfileViewModel
import io.golos.cyber_android.ui.screens.profile_communities.ProfileCommunitiesFragment
import kotlinx.android.synthetic.main.fragment_profile_new.*

class ProfileFragment : FragmentBaseMVVM<FragmentProfileNewBinding, ProfileViewModel>() {
    companion object {
        fun newInstance(userId: String) = ProfileFragment().apply {
            arguments = Bundle().apply { putString(Tags.USER_ID, userId) }
        }
    }

    override fun provideViewModelType(): Class<ProfileViewModel> = ProfileViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_new

    override fun inject() = App.injections.get<ProfileFragmentComponent>(getUser()).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileNewBinding, viewModel: ProfileViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            communitiesVisibility.observe({viewLifecycleOwner.lifecycle}) {
                if(it == View.VISIBLE) {
                    fragmentManager
                        ?.beginTransaction()
                        ?.add(R.id.communitiesContainer, ProfileCommunitiesFragment.newInstance())
                        ?.commit();
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPages()
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is ShowSelectCoverDialogCommand -> {
                ProfilePhotoMenuDialog.newInstance(ProfilePhotoMenuDialog.Type.COVER, this@ProfileFragment)
                    .show(requireFragmentManager(), "menu")
            }
            is ShowSelectAvatarDialogCommand -> {
                ProfilePhotoMenuDialog.newInstance(ProfilePhotoMenuDialog.Type.AVATAR, this@ProfileFragment)
                    .show(requireFragmentManager(), "menu")
            }
        }
    }

    private fun getUser() = (arguments?.getString(Tags.USER_ID) ?: "").toCyberName()

    private fun initPages() {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(context.resources.getColorRes(R.color.blue))
        }

        vpContent.post{
            vpContent.adapter = ProfilePagesAdapter(context!!.applicationContext, childFragmentManager)
            vpContent.offscreenPageLimit = 2
        }
    }
}