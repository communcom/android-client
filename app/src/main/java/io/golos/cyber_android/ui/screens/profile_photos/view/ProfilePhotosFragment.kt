package io.golos.cyber_android.ui.screens.profile_photos.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfilePhotosBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.PhotoPlace
import io.golos.cyber_android.ui.screens.profile_photos.dto.InitPhotoPreviewCommand
import io.golos.cyber_android.ui.screens.profile_photos.view_model.ProfilePhotosViewModel
import kotlinx.android.synthetic.main.fragment_profile_photos.*

class ProfilePhotosFragment : FragmentBaseMVVM<FragmentProfilePhotosBinding, ProfilePhotosViewModel>() {
    companion object {
        private const val PLACE = "PLACE"
        private const val IMAGE_URL = "IMAGE_URL"

        fun newInstance(place: PhotoPlace, imageUrl: String?): ProfilePhotosFragment {
            return ProfilePhotosFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLACE, place.value)
                    putString(IMAGE_URL, imageUrl)
                }
            }
        }
    }

    override fun provideViewModelType(): Class<ProfilePhotosViewModel> = ProfilePhotosViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_photos

    override fun inject() =
        App.injections
            .get<ProfilePhotosFragmentComponent>(
                PhotoPlace.create(arguments!!.getInt(PLACE)),
                arguments!!.getString(IMAGE_URL))
            .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfilePhotosFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfilePhotosBinding, viewModel: ProfilePhotosViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is BackCommand -> requireActivity().onBackPressed()
            is InitPhotoPreviewCommand -> initPhotoPreview(command.photoPlace, command.imageUrl)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewImage.setOnLoadingCompleteListener { viewModel.onPreviewLoadingCompleted() }

        viewModel.start()
    }

    private fun initPhotoPreview(place: PhotoPlace, imageUrl: String?) {
        previewImage.setMode(place)
        previewImage.load(imageUrl)
    }
}