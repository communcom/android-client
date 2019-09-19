package io.golos.cyber_android.ui.shared_fragments.editor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import im.delight.android.webview.AdvancedWebView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.shared_fragments.post.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.ui.shared_fragments.editor.dialogs.one_text_line.OneTextLineDialog
import io.golos.cyber_android.ui.shared_fragments.editor.dialogs.text_and_link.TextAndLinkDialog
import io.golos.cyber_android.utils.ValidationConstants
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.TextWatcherBase
import io.golos.cyber_android.views.utils.colorizeHashTags
import io.golos.cyber_android.views.utils.colorizeLinks
import io.golos.data.errors.AppError
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import io.golos.posts_editor.dialogs.selectColor.SelectColorDialog
import io.golos.posts_editor.utilities.MaterialColor
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_editor_page.*
import javax.inject.Inject

const val GALLERY_REQUEST = 101

class EditorPageFragment : ImagePickerFragmentBase() {
    @Parcelize
    data class Args(
        val postToEdit: DiscussionIdModel? = null,
        val community: CommunityModel? = null,
        val initialImageSource: ImageSource = ImageSource.NONE
    ): Parcelable

    private lateinit var viewModel: EditorPageViewModel

    @Inject
    lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = getArgs()
        App.injections.get<EditorPageFragmentComponent>(args.community, args.postToEdit).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        linkPreviewWebView?.onDestroy()
        App.injections.release<EditorPageFragmentComponent>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editor_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
        observeViewModel()

        close.setOnClickListener { activity?.finish() }
        post.setOnClickListener { viewModel.post() }

        title.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onTitleChanged(s.toString())
            }
        })

        content.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.onContentChanged(it)
                }
                s?.colorizeHashTags()
                s?.colorizeLinks()
            }
        })

        title.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        title.filters = arrayOf(InputFilter.LengthFilter(ValidationConstants.MAX_POST_TITLE_LENGTH))
        content.filters = arrayOf(InputFilter.LengthFilter(ValidationConstants.MAX_POST_CONTENT_LENGTH))

        setupToolButtons()

        linkPreviewImageClear.setOnClickListener {
            viewModel.clearPickedImage()
        }

        with(linkPreviewWebView) {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            //workaround for "bitmap.getWidth()" exception
            webChromeClient = object : WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap? {
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return true
                }
            }

            setListener(requireActivity(), object : AdvancedWebView.Listener {
                override fun onPageFinished(url: String?) {
                    showPreviewLayout()
                }

                override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
                    onEmbedError()
                }

                override fun onDownloadRequested(
                    url: String?,
                    suggestedFilename: String?,
                    mimeType: String?,
                    contentLength: Long,
                    contentDisposition: String?,
                    userAgent: String?
                ) {
                }

                override fun onExternalPageRequest(url: String?) {
                }

                override fun onPageStarted(url: String?, favicon: Bitmap?) {
                }

            })
        }
    }

    private fun setupToolButtons() {
        nsfw.setOnClickListener {
            viewModel.switchNSFW()
        }

        textColorButton.setOnClickListener {
            SelectColorDialog(this.requireContext(), MaterialColor.BLACK, R.string.select_text_color, R.string.ok, R.string.cancel) { selectedColor ->
                selectedColor?.let {  }
            }
            .show()
        }

        tagButton.setOnClickListener {
            OneTextLineDialog(requireContext(), "", R.string.enter_tag) { tag ->

            }
            .show()
        }

        mentionButton.setOnClickListener {
            OneTextLineDialog(requireContext(), "", R.string.enter_user_name) { tag ->

            }
            .show()
        }

        linkInTextButton.setOnClickListener {
            TextAndLinkDialog(requireContext(), "", "", R.string.enter_link) { text, link ->

            }
            .show()
        }

        photo.setOnClickListener {
            ImagePickerDialog.newInstance(ImagePickerDialog.Target.EDITOR_PAGE).apply {
                setTargetFragment(this@EditorPageFragment, GALLERY_REQUEST)
            }.show(requireFragmentManager(), "cover")
        }

        linkExternalButton.setOnClickListener {
            OneTextLineDialog(requireContext(), "", R.string.enter_link) { tag ->

            }
            .show()
        }
    }

    private fun setupCommunity(community: CommunityModel) {
        communityName.text = community.name
        //todo return later
//        Glide.with(this)
//            .load(community.avatarUrl)
//            .apply(RequestOptions.circleCropTransform())
//            .into(communityAvatar)
    }

    private fun setupPostToEdit(post: PostModel) {
        toolbarTitle.setText(R.string.edit_post)
        title.setText(post.content.title)
        content.setText(post.content.body.toContent())
        nsfw.isActivated = post.content.tags.contains(TagModel("nsfw"))
        post.content.body.embeds.find { it.type == "photo" || it.html.isEmpty() }?.run {
            viewModel.onRemoteImagePicked(this.url)
        }
        viewModel.consumePostToEdit()
    }

    private fun observeViewModel() {
        viewModel.getValidationResultLiveData.observe(this, Observer {
            post.isEnabled = it
            post.alpha = if (it) 1f else 0.3f
        })

        viewModel.getEmbedLiveDate.observe(this, Observer { result ->
            when (result) {
                is QueryResult.Loading -> onEmbedLoading()
                is QueryResult.Success -> onEmbedResult(result.originalQuery)
                is QueryResult.Error -> onEmbedError()
            }
        })

        viewModel.getEmptyEmbedLiveData.observe(this, Observer { isEmpty ->
            if (isEmpty)
                hidePreviewLayout()
        })

        viewModel.discussionCreationLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading -> onPostLoading()
                    is QueryResult.Success -> onPostResult(result.originalQuery)
                    is QueryResult.Error -> onPostError(result.error)
                }
            }
        })

        viewModel.getNsfwLiveData.observe(this, Observer {
            nsfw.isActivated = it
        })

        viewModel.getCommunityLiveData.observe(this, Observer {
            if (it != null)
                setupCommunity(it)
        })

        viewModel.getPostToEditLiveData.observe(this, Observer {
            if (it != null)
                setupPostToEdit(it)
        })

        viewModel.getAttachedImageLiveData.observe(this, Observer {
            when {
                it.localUri != null -> loadLocalAttachmentImage(it.localUri)
                it.remoteUrl != null -> loadRemoteAttachmentImage(it.remoteUrl)
                else -> clearUserPickedImage()
            }
        })

        viewModel.getFileUploadingStateLiveData.asEvent().observe(this, Observer { event ->
            event.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading -> onPostLoading()
                    is QueryResult.Error -> onPostError(result.error)
                }
            }
        })
    }

    private fun onPostError(error: Throwable) {
        val errorMsg = when (error) {
            is AppError.NotEnoughPowerError -> R.string.not_enough_power
            is AppError.RequestTimeOutException -> R.string.request_timeout_error
            else -> R.string.unknown_error
        }
        NotificationDialog.newInstance(getString(errorMsg))
            .show(requireFragmentManager(), "create error")
        hideLoading()
    }

    private fun onPostResult(result: DiscussionCreationResultModel) {
        hideLoading()
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
        if (result is PostCreationResultModel) {
            startActivity(
                PostActivity.getIntent(
                    requireContext(), PostPageFragment.Args(
                        result.postId
                    )
                )
            )
        }
    }

    private fun onPostLoading() {
        showLoading()
    }

    private fun onEmbedResult(model: LinkEmbedModel) {
        previewSummary.text = model.summary
        previewProvider.text = model.provider
        previewSummary.visibility = if (model.summary.isBlank()) View.GONE else View.VISIBLE
        previewProvider.visibility = if (model.provider.isBlank()) View.GONE else View.VISIBLE
        previewDescriptionLayout.visibility =
            if (model.summary.isBlank() && model.provider.isBlank()) View.GONE else View.VISIBLE
        linkPreviewImageClear.visibility = View.GONE

        if (model.embedHtml.isNotBlank()) {
            loadEmbeddedHtml(model)
        } else {
            if (model.thumbnailImageUrl.isNotBlank()) {
                loadThumbnail(model)
            }
        }
    }

    override fun getInitialImageSource() = getArgs().initialImageSource

    override fun onImagePicked(uri: Uri) {
        viewModel.onLocalImagePicked(uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST) {
            when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY ->
                    pickGalleryPhoto()
                ImagePickerDialog.RESULT_CAMERA ->
                    takeCameraPhoto()
            }
        }
    }

    private fun loadLocalAttachmentImage(uri: Uri) {
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageViewLayout.visibility = View.VISIBLE
        linkPreviewImageClear.visibility = View.VISIBLE
        previewDescriptionLayout.visibility = View.GONE
        showPreviewLayout()


        Glide.with(requireContext())
            .load(uri)
            .fitCenter()
            .into(linkPreviewImageView)
    }

    private fun loadRemoteAttachmentImage(url: String) {
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageViewLayout.visibility = View.VISIBLE
        linkPreviewImageClear.visibility = View.VISIBLE
        previewDescriptionLayout.visibility = View.GONE
        showPreviewLayout()


        Glide.with(requireContext())
            .load(url)
            .fitCenter()
            .into(linkPreviewImageView)
    }

    private fun clearUserPickedImage() {
        hidePreviewLayout()

        Glide.with(requireContext())
            .load(0)
            .fitCenter()
            .into(linkPreviewImageView)
    }

    private fun loadThumbnail(model: LinkEmbedModel) {
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageViewLayout.visibility = View.VISIBLE
        showPreviewLayout()

        Glide.with(requireContext())
            .load(model.thumbnailImageUrl)
            .fitCenter()
            .into(linkPreviewImageView)
    }

    private fun loadEmbeddedHtml(model: LinkEmbedModel) {
        showPreviewLayout()
        linkPreviewWebView.visibility = View.VISIBLE
        linkPreviewImageViewLayout.visibility = View.GONE
        linkPreviewWebView.loadDataWithBaseURL(model.url, model.embedHtml, "text/html", "UTF-8", null)
    }

    private fun onEmbedError() {
        hidePreviewLayout()
        linkPreviewProgress.visibility = View.GONE
        Toast.makeText(requireContext(), "Embedded content error", Toast.LENGTH_SHORT).show()
    }

    private fun onEmbedLoading() {
        linkPreviewProgress.visibility = View.VISIBLE
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageViewLayout.visibility = View.GONE
    }

    private fun hidePreviewLayout() {
        linkPreviewLayout.visibility = View.GONE
    }

    private fun showPreviewLayout() {
        linkPreviewLayout.visibility = View.VISIBLE
        linkPreviewProgress.visibility = View.GONE
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditorPageViewModel::class.java)
    }

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    override fun onPause() {
        linkPreviewWebView.onPause()
        super.onPause()
    }

    override fun onResume() {
        linkPreviewWebView.onResume()
        super.onResume()
    }

    override fun onImagePickingCancel() {
        //noop
    }
}
