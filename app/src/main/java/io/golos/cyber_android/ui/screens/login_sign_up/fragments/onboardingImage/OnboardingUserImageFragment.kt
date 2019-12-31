package io.golos.cyber_android.ui.screens.login_sign_up.fragments.onboardingImage

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.common.extensions.safeNavigate
import io.golos.cyber_android.ui.common.extensions.setPadding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.trash.ImagePickerFragmentBase
import io.golos.cyber_android.ui.screens.login_sign_up_bio.OnboardingBioFragment
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_onboarding_user_image.*
import javax.inject.Inject

const val REQUEST_UPDATE_PHOTO_DIALOG = 102
const val REQUEST_UPDATE_PHOTO = 103


class OnboardingUserImageFragment : FragmentBase() {
    @Parcelize
    data class Args(
        val userCyberName: String
    ): Parcelable

    private lateinit var viewModel: OnboardingUserImageViewModel

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<OnBoardingFragmentComponent>(CyberName(getArgs().userCyberName)).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<OnBoardingFragmentComponent>()
    }

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
                setTargetFragment(this@OnboardingUserImageFragment,
                    REQUEST_UPDATE_PHOTO_DIALOG
                )
            }.show(requireFragmentManager(), "cover")
        }
        skip.setOnClickListener {
            goToOnboadingBioScreen()
        }
        next.setOnClickListener {
            goToOnboadingBioScreen()
        }
    }

    private fun goToOnboadingBioScreen() {
        findNavController().safeNavigate(
            R.id.onboardingUserImageFragment,
            R.id.action_onboardingUserImageFragment_to_onboardingBioFragment,
            Bundle().apply { putParcelable( Tags.ARGS,
                OnboardingBioFragment.Args(getArgs().userCyberName)) }
        )
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
                    ImagePickerFragmentBase.ImageSource.GALLERY
                ImagePickerDialog.RESULT_CAMERA ->
                    ImagePickerFragmentBase.ImageSource.CAMERA
                else -> null
            }
//            if (target != null) startActivityForResult(
//                EditProfileAvatarActivity
//                    .getIntent(
//                        requireContext(),
//                        EditProfileAvatarFragment.Args(getArgs().userCyberName, target, forOnboarding = true)
//                    ), REQUEST_UPDATE_PHOTO
//            )
        }
    }

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OnboardingUserImageViewModel::class.java)
    }
}
