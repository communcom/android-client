package io.golos.cyber_android.ui.screens.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarActivity
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarFragment
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditProfileBioActivity
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditProfileBioFragment
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverActivity
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverFragment
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsActivity
import io.golos.cyber_android.ui.screens.profile.posts.UserPostsFeedFragment
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.TabLayoutMediator
import io.golos.domain.interactors.model.UserMetadataModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*


const val REQUEST_UPDATE_COVER_DIALOG = 101
const val REQUEST_UPDATE_PHOTO_DIALOG = 102
const val REQUEST_UPDATE_COVER = 103
const val REQUEST_UPDATE_PHOTO = 104

class ProfileFragment : LoadingFragment() {

    enum class Tab(@StringRes val title: Int, val index: Int) {
        POSTS(R.string.posts, 0), COMMENTS(R.string.comments, 1)
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupViewModel()
        observeViewModel()

        settings.setOnClickListener { startActivity(ProfileSettingsActivity.getIntent(requireContext())) }
        updateCover.setOnClickListener {
            ImagePickerDialog.newInstance(ImagePickerDialog.Target.COVER).apply {
                setTargetFragment(this@ProfileFragment, REQUEST_UPDATE_COVER_DIALOG)
            }.show(requireFragmentManager(), "cover")
        }


        updatePhoto.setOnClickListener {
            ImagePickerDialog.newInstance(ImagePickerDialog.Target.AVATAR).apply {
                setTargetFragment(this@ProfileFragment, REQUEST_UPDATE_PHOTO_DIALOG)
            }.show(requireFragmentManager(), "cover")
        }


        followersPhotosView.setPhotosUrls(listOf("", "", ""))
        followingPhotosView.setPhotosUrls(listOf("", ""))
        subscribersPhotosView.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.getProfileLiveData.observe(this, Observer { result ->
            when (result) {
                is QueryResult.Success -> {
                    bindProfile(result.originalQuery.metadata)
                    updateControlsVisibility(result.originalQuery.isMyUser)
                }
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> onLoading()
            }
        })

        viewModel.getMetadataUpdateStateLiveData.asEvent().observe(this, Observer {event ->
            event.getIfNotHandled()?.let {
                when (it) {
                    is QueryResult.Success -> hideLoading()
                    is QueryResult.Error -> onError()
                    is QueryResult.Loading -> showLoading()
                }
            }
        })
    }

    private fun onLoading() {
        Toast.makeText(requireContext(), "loading profile", Toast.LENGTH_SHORT).show()
    }

    private fun onError() {
        Toast.makeText(requireContext(), "loading profile error", Toast.LENGTH_SHORT).show()
    }

    private fun bindProfile(profile: UserMetadataModel) {
        updateEditBioControls(profile)
        updatePersonalData(profile)
        updateStats(profile)
        updateFollowing(profile)
    }

    private fun updateFollowing(profile: UserMetadataModel) {
        follow.isActivated = profile.isSubscribed
        follow.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(
                requireContext(),
                if (profile.isSubscribed) R.drawable.ic_following_active else R.drawable.ic_following_inactive
            ),
            null, null
        )
    }

    private fun updateStats(profile: UserMetadataModel) {
        followersCount.text = (profile.subscribers.usersCount + profile.subscribers.communitiesCount).toString()
        followingsCount.text = (profile.subscriptions.usersCount + profile.subscriptions.communitiesCount).toString()
    }

    private fun updatePersonalData(profile: UserMetadataModel) {
        if (profile.personal.coverUrl.isNullOrBlank())
            cover.setImageDrawable(null)
        else Glide.with(requireContext()).load(profile.personal.coverUrl)
            .into(cover)

        avatarText.text = profile.username

        if (profile.personal.avatarUrl.isNullOrBlank()) {
            avatar.setImageDrawable(null)
            avatarText.visibility = View.VISIBLE
        } else {
            Glide.with(requireContext())
                .load(profile.personal.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
            avatarText.visibility = View.GONE
        }

        username.text = profile.username

        joined.text = String.format(
            getString(R.string.profile_creation_date_caption_format),
            SimpleDateFormat(
                getString(R.string.profile_creation_date_format),
                Locale.US
            ).format(profile.createdAt)
        )

        bio.text = profile.personal.biography
    }

    private fun updateControlsVisibility(isActiveUserProfile: Boolean) {
        if (isActiveUserProfile) {
            updatePhoto.visibility = View.VISIBLE
            updateCover.visibility = View.VISIBLE
            settings.visibility = View.VISIBLE
            follow.visibility = View.GONE
            sentPoints.visibility = View.GONE
        } else {
            updatePhoto.visibility = View.GONE
            updateCover.visibility = View.GONE
            settings.visibility = View.GONE
            follow.visibility = View.VISIBLE
            sentPoints.visibility = View.VISIBLE
            editBio.visibility = View.GONE
        }
    }

    private fun updateEditBioControls(profile: UserMetadataModel) {
        listOf(bio, editBio).forEach {
            it.setOnClickListener {
                startActivity(
                    EditProfileBioActivity.getIntent(
                        requireContext(),
                        EditProfileBioFragment.Args(
                            profile.userId,
                            profile.personal.biography ?: ""
                        )
                    )
                )
            }
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, profilePager) { tab, position ->
            tab.setText(Tab.values()[position].title)
        }.attach()
    }

    private fun setupViewPager() {
        profilePager.adapter = object : FragmentStateAdapter(requireFragmentManager(), this.lifecycle) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    Tab.POSTS.index -> UserPostsFeedFragment.newInstance(getUserName())
                    Tab.COMMENTS.index -> UserPostsFeedFragment.newInstance(getUserName())
                    else -> throw RuntimeException("Unsupported tab")
                }
            }

            override fun getItemCount() = Tab.values().size

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_COVER_DIALOG) {
            val target = when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    EditProfileCoverFragment.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    EditProfileCoverFragment.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE -> {
                    viewModel.clearProfileCover()
                    null
                }
                else -> null
            }
            if (target != null) startActivityForResult(
                EditProfileCoverActivity
                    .getIntent(
                        requireContext(),
                        EditProfileCoverFragment.Args(viewModel.forUser, target)
                    ), REQUEST_UPDATE_COVER
            )
        }

        if (requestCode == REQUEST_UPDATE_PHOTO_DIALOG) {
            val target = when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    EditProfileCoverFragment.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    EditProfileCoverFragment.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE -> {
                    viewModel.clearProfileAvatar()
                    null
                }
                else -> null
            }
            if (target != null) startActivityForResult(
                EditProfileAvatarActivity
                    .getIntent(
                        requireContext(),
                        EditProfileAvatarFragment.Args(viewModel.forUser, target)
                    ), REQUEST_UPDATE_PHOTO
            )
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getProfileViewModelFactory(getUserName().toCyberName())
        ).get(ProfileViewModel::class.java)
    }

    private fun getUserName() = (arguments?.getString(Tags.USER_ID) ?: "")

    companion object {
        fun newInstance(userId: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(Tags.USER_ID, userId)
            }
        }
    }

}
