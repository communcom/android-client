package io.golos.cyber_android.ui.screens.profile.edit.avatar

import android.graphics.Point
import android.graphics.Rect
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
import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity.EditProfileAvatarActivityComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.edit_profile_avatar_fragment.*
import java.io.File
import javax.inject.Inject


class EditProfileAvatarFragment : ImagePickerFragmentBase() {
    @Parcelize
    data class Args(
        val userCyberName: String,
        val source: ImageSource,
        val forOnboarding: Boolean = false
    ): Parcelable

    private lateinit var viewModel: EditProfileAvatarViewModel

    private var selectedUri: Uri? = null

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<EditProfileAvatarActivityComponent>(CyberName(getArgs().userCyberName)).inject(this)
    }

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

        if (getArgs().forOnboarding) {
            toolbarTitle.text = ""
        }
    }

    private fun observeViewModel() {
        viewModel.getFileUploadingStateLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> showLoading()
                is QueryResult.Success -> viewModel.updateAvatar(it.originalQuery.url, !getArgs().forOnboarding)
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditProfileAvatarViewModel::class.java)
    }

    override fun getInitialImageSource() = getArgs().source

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    companion object {
        fun newInstance(serializedArgs: Args) =
            EditProfileAvatarFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Tags.ARGS, serializedArgs)
                }
            }
    }


}
