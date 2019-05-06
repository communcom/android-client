package io.golos.cyber_android.ui.screens.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.screens.feed.AllFeedFragment
import io.golos.cyber_android.ui.screens.feed.FeedPageLiveDataProvider
import io.golos.cyber_android.ui.screens.feed.FeedPageViewModel
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarActivity
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditBioActivity
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverActivity
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsActivity
import io.golos.cyber_android.views.utils.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*


const val REQUEST_UPDATE_COVER_DIALOG = 101
const val REQUEST_UPDATE_PHOTO_DIALOG = 102
const val REQUEST_UPDATE_COVER = 103
const val REQUEST_UPDATE_PHOTO = 104

class ProfileFragment : Fragment(), FeedPageLiveDataProvider {
    override fun provideEventsLiveData(): LiveData<FeedPageViewModel.Event> = MutableLiveData<FeedPageViewModel.Event>()

    enum class Tab(@StringRes val title: Int, val index: Int) {
        POSTS(R.string.posts, 0), COMMENTS(R.string.comments, 1)
    }

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

        editBio.setOnClickListener { startActivity(EditBioActivity.getIntent(requireContext())) }
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

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, profilePager) { tab, position ->
            tab.setText(Tab.values()[position].title)
        }.attach()
    }

    private fun setupViewPager() {
        profilePager.adapter = object : FragmentStateAdapter(requireFragmentManager(), this.lifecycle) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    Tab.POSTS.index -> AllFeedFragment.newInstance("gls").apply {
                        setTargetFragment(this@ProfileFragment, 100)
                    }
                    Tab.COMMENTS.index -> AllFeedFragment.newInstance("gls")
                        .apply {
                            setTargetFragment(this@ProfileFragment, 100)
                        }
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
                    EditProfileCoverActivity.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    EditProfileCoverActivity.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE -> {
                    Toast.makeText(requireContext(), "Delete cover", Toast.LENGTH_SHORT).show()
                    null
                }
                else -> null
            }
            if (target != null) startActivityForResult(
                EditProfileCoverActivity
                    .getIntent(
                        requireContext(),
                        target
                    ), REQUEST_UPDATE_COVER
            )
        }

        if (requestCode == REQUEST_UPDATE_PHOTO_DIALOG) {
            val target = when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    EditProfileCoverActivity.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    EditProfileCoverActivity.ImageSource.CAMERA
                ImagePickerDialog.RESULT_DELETE -> {
                    Toast.makeText(requireContext(), "Delete cover", Toast.LENGTH_SHORT).show()
                    null
                }
                else -> null
            }
            if (target != null) startActivityForResult(
                EditProfileAvatarActivity
                    .getIntent(
                        requireContext(),
                        target
                    ), REQUEST_UPDATE_PHOTO
            )
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

}
