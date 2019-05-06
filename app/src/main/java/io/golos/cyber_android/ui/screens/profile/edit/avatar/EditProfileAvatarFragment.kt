package io.golos.cyber_android.ui.screens.profile.edit.avatar

import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.profile.edit.BaseProfileImageFragment
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverActivity
import io.golos.domain.interactors.model.UploadedImageModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.edit_profile_avatar_fragment.*
import java.io.File


class EditProfileAvatarFragment : BaseProfileImageFragment() {

    private lateinit var viewModel: EditProfileAvatarViewModel

    private var selectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_profile_avatar_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        //make squared avatar
        (avatar.layoutParams as ViewGroup.LayoutParams).height = size.x

        rotate.setOnClickListener {
            avatar.rotation = avatar.rotation - 90f
        }
        close.setOnClickListener { requireActivity().finish() }
        post.setOnClickListener { confirm() }
    }

    private fun observeViewModel() {
        viewModel.uploadingStateLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> showLoading()
                is QueryResult.Success -> onSuccess(it.originalQuery)
            }
        })
    }

    private fun onSuccess(result: UploadedImageModel) {
        hideLoading()
        Toast.makeText(requireContext(), "result = ${result.url}", Toast.LENGTH_SHORT).show()
    }

    private fun onError() {
        hideLoading()
        Toast.makeText(requireContext(), "uploading image error", Toast.LENGTH_SHORT).show()
    }

    private fun confirm() {
        showLoading()
        selectedUri?.let { uri ->
            val rect = Rect()
            avatar.getGlobalVisibleRect(rect)
            viewModel.uploadFile(File(uri.path), rect.left.toFloat(), rect.top.toFloat(), avatar.rotation)
        }
    }

    override fun onImagePicked(uri: Uri) {
        selectedUri = uri
        Glide.with(requireContext())
            .load(uri)
            .into(avatar)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getEditProfileAvatarViewModelFactory()
        ).get(EditProfileAvatarViewModel::class.java)
    }

    companion object {
        fun newInstance(source: EditProfileCoverActivity.ImageSource) =
            EditProfileAvatarFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Tags.ARGS, source)
                }
            }
    }


}
