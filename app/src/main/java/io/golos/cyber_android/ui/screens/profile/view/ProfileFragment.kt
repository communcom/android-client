package io.golos.cyber_android.ui.screens.profile.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileNewBinding
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.ProfileExternalUserSettingsDialog
import io.golos.cyber_android.ui.dialogs.ProfileMenuDialog
import io.golos.cyber_android.ui.dialogs.ProfileSettingsDialog
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.login_activity.view.LoginActivity
import io.golos.cyber_android.ui.screens.profile.di.ProfileFragmentComponent
import io.golos.cyber_android.ui.screens.profile.dto.*
import io.golos.cyber_android.ui.screens.profile.view.adapters.ProfilePagesAdapter
import io.golos.cyber_android.ui.screens.profile.view_model.ProfileViewModel
import io.golos.cyber_android.ui.screens.profile_bio.view.ProfileBioFragment
import io.golos.cyber_android.ui.screens.profile_black_list.view.ProfileBlackListFragment
import io.golos.cyber_android.ui.screens.profile_communities.view.ProfileCommunitiesFragment
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersFragment
import io.golos.cyber_android.ui.screens.profile_liked.ProfileLikedFragment
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.cyber_android.ui.screens.wallet.view.WalletFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowConfirmationDialog
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.widgets.TabLineDrawable
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_profile_new.*
import java.io.File

open class ProfileFragment : FragmentBaseMVVM<FragmentProfileNewBinding, ProfileViewModel>() {

    companion object {
        fun newInstance(userId: UserIdDomain) = ProfileFragment().apply {
            arguments = Bundle().apply { putParcelable(Tags.USER_ID, userId) }
        }
    }

    override fun provideViewModelType(): Class<ProfileViewModel> = ProfileViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_new

    override fun inject(key: String) =
        App.injections.get<ProfileFragmentComponent>(key, arguments!!.getParcelable<UserIdDomain>(Tags.USER_ID)).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentProfileNewBinding, viewModel: ProfileViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            communities.observe({ viewLifecycleOwner.lifecycle }) {
                it?.let {
                    val fragmentTag = "COMMUNITIES_FRAGMENT"
                    val oldFragment = childFragmentManager.findFragmentByTag(fragmentTag)

                    val transaction = childFragmentManager.beginTransaction()

                    oldFragment?.let { transaction.remove(it) }

                    transaction.add(R.id.communitiesContainer, provideCommunitiesFragment(it), fragmentTag)
                    transaction.commit()
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noConnection.setOnReconnectClickListener { viewModel.onRetryClick() }
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is ShowSelectPhotoDialogCommand -> showPhotoDialog(command.place)
            is ShowEditBioDialogCommand -> showEditBioDialog()
            is NavigateToSelectPhotoPageCommand -> moveToSelectPhotoPage(command.place, command.imageUrl)
            is NavigateToBioPageCommand -> moveToBioPage(command.text)
            is NavigateToFollowersPageCommand -> moveToFollowersPage(command.filter, command.mutualUsers)
            is ShowSettingsDialogCommand -> showSettingsDialog()
            is ShowExternalUserSettingsDialogCommand -> showExternalUserSettingsDialog(command.isBlocked)
            is ShowConfirmationDialog -> showConfirmationDialog(command.textRes)
            is NavigateToLikedPageCommand -> moveToLikedPage()
            is NavigateToBlackListPageCommand -> moveToBlackListPage()
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is RestartAppCommand -> restartApp()
            is LoadPostsAndCommentsCommand -> initPages()
            is NavigateToWalletCommand -> moveToWallet(command.balance)
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ProfileMenuDialog.REQUEST -> {
                val item = ProfileItem.create(data!!.extras.getInt(ProfileMenuDialog.ITEM))
                when (resultCode) {
                    ProfileMenuDialog.RESULT_SELECT -> {
                        viewModel.onSelectMenuChosen(item)
                    }
                    ProfileMenuDialog.RESULT_DELETE -> {
                        viewModel.onDeleteMenuChosen(item)
                    }
                }
            }
            ProfilePhotosFragment.REQUEST -> {
                val result = data!!.extras.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                viewModel.updatePhoto(File(result.photoFilePath), result.place)
            }
            ProfileBioFragment.REQUEST -> {
                viewModel.updateBio(data!!.extras.getString(ProfileBioFragment.RESULT)!!)
            }
            ProfileSettingsDialog.REQUEST -> {
                when (resultCode) {
                    ProfileSettingsDialog.RESULT_LOGOUT -> viewModel.onLogoutSelected()
                    ProfileSettingsDialog.RESULT_LIKED -> viewModel.onLikedSelected()
                    ProfileSettingsDialog.RESULT_BLACK_LIST -> viewModel.onBlackListSelected()
                }
            }
            ProfileExternalUserSettingsDialog.REQUEST -> {
                when (resultCode) {
                    ProfileExternalUserSettingsDialog.RESULT_BLACK_LIST -> viewModel.onMoveToBlackListSelected()
                }
            }
            ConfirmationDialog.REQUEST -> {
                if (resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.onLogoutConfirmed()
                }
            }
        }
    }

    protected open fun provideCommunitiesFragment(sourceData: ProfileCommunities): Fragment =
        ProfileCommunitiesFragment.newInstance(sourceData)

    protected open fun provideFollowersFragment(filter: FollowersFilter, mutualUsers: List<UserDomain>): Fragment =
        ProfileFollowersFragment.newInstance(filter, mutualUsers)

    protected open fun providePagesAdapter(): FragmentStatePagerAdapter = ProfilePagesAdapter(
        context!!.applicationContext,
        getDashboardFragment(this)?.childFragmentManager!!,
        arguments?.getParcelable(Tags.USER_ID)!!) { appbar.setExpanded(false, true) }

    private fun initPages() {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(context.resources.getColorRes(R.color.blue))
        }

        vpContent.post {
            vpContent.adapter = providePagesAdapter()
            vpContent.offscreenPageLimit = 2
        }
    }

    private fun showPhotoDialog(place: ProfileItem) =
        ProfileMenuDialog.newInstance(place, this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun showEditBioDialog() =
        ProfileMenuDialog.newInstance(ProfileItem.BIO, this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun showSettingsDialog() =
        ProfileSettingsDialog.newInstance(this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun showExternalUserSettingsDialog(isBlocked: Boolean) =
        ProfileExternalUserSettingsDialog.newInstance(this@ProfileFragment, isBlocked).show(requireFragmentManager(), "menu")

    private fun showConfirmationDialog(@StringRes textResId: Int) =
        ConfirmationDialog.newInstance(textResId, this@ProfileFragment).show(requireFragmentManager(), "menu")

    private fun moveToSelectPhotoPage(place: ProfileItem, imageUrl: String?) =
        getDashboardFragment(this)
            ?.navigateToFragment(
                ProfilePhotosFragment.newInstance(
                    place,
                    imageUrl,
                    this@ProfileFragment
                )
            )

    private fun moveToBioPage(text: String?) =
        getDashboardFragment(this)?.navigateToFragment(ProfileBioFragment.newInstance(text, this@ProfileFragment))

    private fun moveToFollowersPage(filter: FollowersFilter, mutualUsers: List<UserDomain>) {
        getDashboardFragment(this)?.navigateToFragment(provideFollowersFragment(filter, mutualUsers))
    }

    private fun moveToLikedPage() = getDashboardFragment(this)?.navigateToFragment(ProfileLikedFragment.newInstance())

    private fun moveToBlackListPage() =
        getDashboardFragment(this)?.navigateToFragment(ProfileBlackListFragment.newInstance(BlackListFilter.USERS))

    private fun restartApp() {
        val loginIntent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(loginIntent)
        activity!!.finish()
    }

    private fun moveToWallet(balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.navigateToFragment(WalletFragment.newInstance(balance), tag = WalletFragment.tag)
}