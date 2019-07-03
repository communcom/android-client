package io.golos.cyber_android.ui.screens.login.signup.onboarding.image

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.setPadding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.screens.login.signup.onboarding.bio.OnboardingBioFragment
import io.golos.cyber_android.ui.screens.main.MainActivity
import io.golos.cyber_android.ui.screens.profile.edit.BaseImagePickerFragment
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarActivity
import io.golos.cyber_android.ui.screens.profile.edit.avatar.EditProfileAvatarFragment
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
import kotlinx.android.synthetic.main.fragment_onboarding_user_image.*

const val REQUEST_UPDATE_PHOTO_DIALOG = 102
const val REQUEST_UPDATE_PHOTO = 103


class OnboardingUserImageFragment : LoadingFragment() {

    data class Args(val user: CyberName)

    private lateinit var viewModel: OnboardingUserImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_user_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        image.setOnClickListener {
            ImagePickerDialog.newInstance(ImagePickerDialog.Target.ONBOARDING).apply {
                setTargetFragment(this@OnboardingUserImageFragment, REQUEST_UPDATE_PHOTO_DIALOG)
            }.show(requireFragmentManager(), "cover")
        }
        skip.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
        next.setOnClickListener {
            findNavController().safeNavigate(
                R.id.onboardingUserImageFragment,
                R.id.action_onboardingUserImageFragment_to_onboardingBioFragment,
                Bundle().apply {
                    putString(
                        Tags.ARGS,
                        requireContext()
                            .serviceLocator.moshi
                            .adapter(OnboardingBioFragment.Args::class.java)
                            .toJson(OnboardingBioFragment.Args(getArgs().user))
                    )
                }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.getUserMetadataLiveData.observe(this, Observer {
            if (it is QueryResult.Success)
                if (it.originalQuery.personal.avatarUrl.isNullOrBlank()) {
                    Glide.with(requireContext()).load(R.drawable.ic_sign_up_camera)
                        .into(image)
                    image.setPadding(resources.getDimensionPixelSize(R.dimen.padding_sign_up_image))
                    next.visibility = View.GONE
                    skip.visibility = View.VISIBLE
                } else {
                    image.setImageResource(0)
                    Glide.with(requireContext()).load(it.originalQuery.personal.avatarUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(image)
                    image.setPadding(0)
                    next.visibility = View.VISIBLE
                    skip.visibility = View.GONE
                }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_PHOTO_DIALOG) {
            val target = when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    BaseImagePickerFragment.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    BaseImagePickerFragment.ImageSource.CAMERA
                else -> null
            }
            if (target != null) startActivityForResult(
                EditProfileAvatarActivity
                    .getIntent(
                        requireContext(),
                        EditProfileAvatarFragment.Args(getArgs().user, target, forOnboarding = true)
                    ), REQUEST_UPDATE_PHOTO
            )
        }
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireContext()
                .serviceLocator
                .getViewModelFactoryByCyberName(getArgs().user)
        )
            .get(OnboardingUserImageViewModel::class.java)
    }

}
