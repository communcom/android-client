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
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.profile.edit.BaseImagePickerFragment
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverFragment
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.edit_profile_avatar_fragment.*
import java.io.File


class EditProfileAvatarFragment : BaseImagePickerFragment() {

    data class Args(
        val user: CyberName,
        val source: ImageSource
    )

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
        viewModel.getFileUploadingStateLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> showLoading()
                is QueryResult.Success -> viewModel.updateAvatar(it.originalQuery.url)
            }
        })

        viewModel.getMetadataUpdateStateLiveData.asEvent().observe(this, Observer { event ->
            event?.getIfNotHandled()?.let {
                when (it) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Error -> onError()
                    is QueryResult.Success -> onSuccess()
                }
            }
        })
    }

    private fun onSuccess() {
        hideLoading()
        requireActivity().finish()
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
                .getViewModelFactoryByCyberName(getArgs().user)
        ).get(EditProfileAvatarViewModel::class.java)
    }

    override fun getInitialImageSource() = getArgs().source

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!


    companion object {
        fun newInstance(serializedArgs: String) =
            EditProfileAvatarFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.ARGS, serializedArgs)
                }
            }
    }


}
