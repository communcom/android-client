package io.golos.cyber_android.ui.screens.profile.edit.cover

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity.EditProfileCoverActivityComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.TouchImageView
import io.golos.domain.interactors.model.UserMetadataModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.edit_profile_cover_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditProfileCoverFragment : ImagePickerFragmentBase() {
    @Parcelize
    data class Args(
        val userCyberName: String,
        val source: ImageSource
    ): Parcelable

    private lateinit var viewModel: EditProfileCoverViewModel

    private var selectedUri: Uri? = null

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<EditProfileCoverActivityComponent>(CyberName(getArgs().userCyberName)).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_profile_cover_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        close.setOnClickListener { requireActivity().finish() }
        post.setOnClickListener { confirm() }
    }

    private fun observeViewModel() {
        viewModel.getFileUploadingStateLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> showLoading()
                is QueryResult.Success -> viewModel.updateCover(it.originalQuery.url)
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

        viewModel.getMetadataLiveData.observe(this, Observer { result ->
            when (result) {
                is QueryResult.Success -> bindProfile(result.originalQuery)
            }
        })
    }

    private fun bindProfile(profile: UserMetadataModel) {
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
        val zoomedRect = coverImage.zoomedRect
        selectedUri?.let { uri ->
            viewModel.uploadFile(File(uri.path), zoomedRect.left, zoomedRect.top, zoomedRect.width(), zoomedRect.height())
        }
    }

    override fun onImagePicked(uri: Uri) {
        selectedUri = uri
        Glide.with(requireContext())
            .load(uri)
            .into(object : CustomViewTarget<TouchImageView, Drawable>(coverImage) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    coverImage.setImageDrawable(resource)
                }

            })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditProfileCoverViewModel::class.java)
    }

    override fun getInitialImageSource() = getArgs().source

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    companion object {
        fun newInstance(serializedArgs: Args) =
            EditProfileCoverFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Tags.ARGS, serializedArgs)
                }
            }
    }
}
