package io.golos.cyber_android.ui.screens.profile.new_profile.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.golos.commun4j.sharedmodel.CyberName
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
import io.golos.cyber_android.ui.dialogs.ProfileMenuDialog
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.profile.new_profile.dto.*
import io.golos.cyber_android.ui.screens.profile.new_profile.view_model.ProfileViewModel
import io.golos.cyber_android.ui.screens.profile_bio.view.ProfileBioFragment
import io.golos.cyber_android.ui.screens.profile_communities.view.ProfileCommunitiesFragment
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersFragment
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.domain.dto.UserDomain
import kotlinx.android.synthetic.main.fragment_profile_new.*
import java.io.File

class ProfileFragment : FragmentBaseMVVM<FragmentProfileNewBinding, ProfileViewModel>() {
    companion object {
        fun newInstance(userId: String) = ProfileFragment().apply {
            arguments = Bundle().apply { putString(Tags.USER_ID, userId) }
        }
    }

    private val user: CyberName
        get()= (arguments?.getString(Tags.USER_ID) ?: "").toCyberName()

    override fun provideViewModelType(): Class<ProfileViewModel> = ProfileViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_new

    override fun inject() = App.injections.get<ProfileFragmentComponent>(user).inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileNewBinding, viewModel: ProfileViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            communities.observe({viewLifecycleOwner.lifecycle}) {
                it?.let {
                    fragmentManager
                        ?.beginTransaction()
                        ?.add(R.id.communitiesContainer, ProfileCommunitiesFragment.newInstance(it))
                        ?.commit()
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
            is ShowSelectPhotoDialogCommand -> showPhotoDialog(command.place)
            is ShowEditBioDialogCommand -> showEditBioDialog()
            is MoveToSelectPhotoPageCommand -> moveToSelectPhotoPage(command.place)
            is MoveToBioPageCommand -> moveToBioPage(command.text)
            is MoveToFollowersPageCommand -> moveToFollowersPage(command.filter, command.mutualUsers)
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ProfileMenuDialog.REQUEST -> {
                val item = ProfileItem.create(data!!.extras.getInt(ProfileMenuDialog.ITEM))
                when (resultCode) {
                    ProfileMenuDialog.RESULT_SELECT -> { viewModel.onSelectMenuChosen(item)}
                    ProfileMenuDialog.RESULT_DELETE -> { viewModel.onDeleteMenuChosen(item) }
                }
            }
            ProfilePhotosFragment.REQUEST -> {
                val result = data!!.extras.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                viewModel.updatePhoto(File(result.photoFilePath), result.place)
            }
            ProfileBioFragment.REQUEST -> {
                viewModel.updateBio(data!!.extras.getString(ProfileBioFragment.RESULT)!!)
            }
        }
    }

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

    private fun showPhotoDialog(place: ProfileItem) =
        ProfileMenuDialog.newInstance(place, this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun showEditBioDialog() =
        ProfileMenuDialog.newInstance(ProfileItem.BIO, this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun moveToSelectPhotoPage(place: ProfileItem) =
        (requireActivity() as MainActivity)
            .showFragment(
                ProfilePhotosFragment.newInstance(
                    place,
                    "https://images.unsplash.com/photo-1506598417715-e3c191368ac0?ixlib=rb-1.2.1&w=1000&q=80",
                    this@ProfileFragment))

    private fun moveToBioPage(text: String?) =
        (requireActivity() as MainActivity).showFragment(ProfileBioFragment.newInstance(text, this@ProfileFragment))

    private fun moveToFollowersPage(filter: FollowersFilter, mutualUsers: List<UserDomain>) {
        (requireActivity() as MainActivity).showFragment(ProfileFollowersFragment.newInstance(filter, mutualUsers))
    }
}