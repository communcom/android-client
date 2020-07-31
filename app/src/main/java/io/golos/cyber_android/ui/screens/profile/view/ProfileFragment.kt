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
import io.golos.cyber_android.ui.dialogs.*
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.WelcomeActivity
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
import io.golos.utils.getColorRes
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToWalletCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowConfirmationDialog
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.widgets.TabLineDrawable
import io.golos.domain.GlobalConstants
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.notifications.NotificationSettingsDomain
import kotlinx.android.synthetic.main.fragment_profile_new.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

open class ProfileFragment : FragmentBaseMVVM<FragmentProfileNewBinding, ProfileViewModel>() {

    @Inject
    lateinit var keyValueStorageFacade: KeyValueStorageFacade

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
            is ShowNotificationsSettingsDialogCommand -> showNotificationsSettingsDialog(command.sourceNotifications)
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
            ProfilePhotosFragment.REQUEST -> {
                val result = data!!.extras.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                viewModel.updatePhoto(File(result.photoFilePath), result.place)
            }
            ProfileBioFragment.REQUEST -> {
                viewModel.updateBio(data!!.extras.getString(ProfileBioFragment.RESULT)!!)
            }
            ConfirmationDialog.REQUEST -> {
                if (resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.onLogoutConfirmed()
                }
            }
        }
    }

    fun scrollToTop() {
        appbar.setExpanded(true)
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
        ProfileMenuDialog.show(this@ProfileFragment, place) {
            when(it) {
                is ProfileMenuDialog.Result.Select -> viewModel.onSelectMenuChosen(it.place)
                is ProfileMenuDialog.Result.Delete -> viewModel.onDeleteMenuChosen(it.place)
            }
        }

    private fun showEditBioDialog() = showPhotoDialog(ProfileItem.BIO)

    private fun showSettingsDialog() =
        ProfileSettingsDialog.show(this@ProfileFragment,
            keyValueStorageFacade.getUIMode() == GlobalConstants.UI_MODE_DARK
        ) {
            when(it) {
                is ProfileSettingsDialog.Result.Logout -> viewModel.onLogoutSelected()
                is ProfileSettingsDialog.Result.Liked -> viewModel.onLikedSelected()
                is ProfileSettingsDialog.Result.BlackList -> viewModel.onBlackListSelected()
                is ProfileSettingsDialog.Result.Notifications -> viewModel.onNotificationsSettingsSelected()
                is ProfileSettingsDialog.Result.SwitchTheme -> switchTheme()
            }
        }

    private fun switchTheme() {
        keyValueStorageFacade.setUIMode(
            if(keyValueStorageFacade.getUIMode() == GlobalConstants.UI_MODE_DARK) GlobalConstants.UI_MODE_LIGHT
            else GlobalConstants.UI_MODE_DARK
        )
        startActivity(Intent(requireActivity(),WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun showNotificationsSettingsDialog(sourceNotifications: List<NotificationSettingsDomain>) =
        ProfileNotificationsDialog.show(this@ProfileFragment, sourceNotifications) {
            viewModel.saveNotificationsSettings(it!!.settings)
        }

    private fun showExternalUserSettingsDialog(isBlocked: Boolean) =
        ProfileExternalUserSettingsDialog.show(this@ProfileFragment, isBlocked) {
            when(it) {
                is ProfileExternalUserSettingsDialog.Result.BlackList -> viewModel.onMoveToBlackListSelected()
            }
        }

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
        val loginIntent = Intent(requireContext(), WelcomeActivity::class.java)
        startActivity(loginIntent)
        activity!!.finish()
    }

    private fun moveToWallet(balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.navigateToFragment(WalletFragment.newInstance(balance), tag = WalletFragment.tag)
}