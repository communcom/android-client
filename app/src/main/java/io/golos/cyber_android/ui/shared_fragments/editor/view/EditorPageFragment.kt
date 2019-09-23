package io.golos.cyber_android.ui.shared_fragments.editor.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.ui.shared_fragments.editor.view_model.EditorPageViewModel
import io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.one_text_line.OneTextLineDialog
import io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.text_and_link.TextAndLinkDialog
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.InsertExternalLinkViewCommand
import io.golos.cyber_android.ui.shared_fragments.post.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.PostPageFragment
import io.golos.cyber_android.utils.ValidationConstants
import io.golos.cyber_android.views.utils.TextWatcherBase
import io.golos.data.errors.AppError
import io.golos.domain.interactors.model.*
import io.golos.posts_editor.dialogs.selectColor.SelectColorDialog
import io.golos.posts_editor.models.EditorTextStyle
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

        title.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        title.filters = arrayOf(InputFilter.LengthFilter(ValidationConstants.MAX_POST_TITLE_LENGTH))

        setupToolButtons()
    }

    private fun setupToolButtons() {
        nsfwButton.setOnClickListener {
            viewModel.switchNSFW()
        }

        boldButton.setOnClickListener { editorWidget.updateTextStyle(EditorTextStyle.BOLD) }
        italicButton.setOnClickListener { editorWidget.updateTextStyle(EditorTextStyle.ITALIC) }

        textColorButton.setOnClickListener {
            SelectColorDialog(this.requireContext(), MaterialColor.BLACK, R.string.select_text_color, R.string.ok, R.string.cancel) { selectedColor ->
                selectedColor?.let { editorWidget.updateTextColor(it) }
            }
            .show()
        }

        tagButton.setOnClickListener {
            val oldTextOfTag = editorWidget.tryGetTextOfTag()
            OneTextLineDialog(requireContext(), oldTextOfTag ?: "", R.string.enter_tag) { newTextOfTag ->
                newTextOfTag?.let {
                    if(oldTextOfTag == null) {
                        editorWidget.insertTag(it)
                    } else {
                        editorWidget.editTag(it)
                    }
                }
            }
            .show()
        }

        mentionButton.setOnClickListener {
            val oldTextOfMention = editorWidget.tryGetTextOfMention()
            OneTextLineDialog(requireContext(), oldTextOfMention ?: "", R.string.enter_user_name) { newTextOfMention ->
                newTextOfMention?.let {
                    if(oldTextOfMention == null) {
                        editorWidget.insertMention(it)
                    } else {
                        editorWidget.editMention(it)
                    }
                }
            }
            .show()
        }

        linkInTextButton.setOnClickListener {
            val oldLink = editorWidget.tryGetLinkInTextInfo()
            TextAndLinkDialog(requireContext(), oldLink?.text ?: "", oldLink?.url ?: "", R.string.enter_link) { text, url ->
                if(text != null && url != null) {
                    if(oldLink == null) {
                        editorWidget.insertLinkInText(text, url)
                    } else {
                        editorWidget.editLinkInText(text, url)
                    }
                }
            }
            .show()
        }

        photoButton.setOnClickListener {
            ImagePickerDialog.newInstance(ImagePickerDialog.Target.EDITOR_PAGE).apply {
                setTargetFragment(this@EditorPageFragment, GALLERY_REQUEST)
            }.show(requireFragmentManager(), "cover")
        }

        linkExternalButton.setOnClickListener {
            OneTextLineDialog(requireContext(), "", R.string.enter_link) { url ->
                url?.let { viewModel.addExternalLink(it) }
            }
            .show()
        }

        boldButton.isEnabled = false
        italicButton.isEnabled = false
        textColorButton.isEnabled = false

        editorWidget.setOnSelectionTextChangeListener { isSomeTextSelected ->
            boldButton.isEnabled = isSomeTextSelected
            italicButton.isEnabled = isSomeTextSelected
            textColorButton.isEnabled = isSomeTextSelected

            tagButton.isEnabled = !isSomeTextSelected
            mentionButton.isEnabled = !isSomeTextSelected
            linkInTextButton.isEnabled = !isSomeTextSelected

            photoButton.isEnabled = !isSomeTextSelected
            linkExternalButton.isEnabled = !isSomeTextSelected
        }
    }

    private fun setupCommunity(community: CommunityModel) {
        communityName.text = community.name
    }

    private fun setupPostToEdit(post: PostModel) {
        // todo [AS] see it later
//        toolbarTitle.setText(R.string.edit_post)
//        title.setText(post.content.title)
//        content.setText(post.content.body.toContent())
//        nsfw.isActivated = post.content.tags.contains(TagModel("nsfw"))
//        post.content.body.embeds.find { it.type == "photo" || it.html.isEmpty() }?.run {
//            viewModel.onRemoteImagePicked(this.url)
//        }
//        viewModel.consumePostToEdit()
    }

    private fun observeViewModel() {
        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
                is InsertExternalLinkViewCommand -> editorWidget.insertImage(command.linkInfo.thumbnailUrl, command.linkInfo.description)
                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })

        // todo [AS] see it later
//        viewModel.getValidationResultLiveData.observe(this, Observer {
//            post.isEnabled = it
//            post.alpha = if (it) 1f else 0.3f
//        })
//
//        viewModel.getEmbedLiveDate.observe(this, Observer { result ->
//            when (result) {
//                is QueryResult.Loading -> onEmbedLoading()
//                is QueryResult.Success -> onEmbedResult(result.originalQuery)
//                is QueryResult.Error -> onEmbedError()
//            }
//        })
//
//        viewModel.getEmptyEmbedLiveData.observe(this, Observer { isEmpty ->
//            if (isEmpty)
//                hidePreviewLayout()
//        })
//
//        viewModel.discussionCreationLiveData.observe(this, Observer { event ->
//            event.getIfNotHandled()?.let { result ->
//                when (result) {
//                    is QueryResult.Loading -> onPostLoading()
//                    is QueryResult.Success -> onPostResult(result.originalQuery)
//                    is QueryResult.Error -> onPostError(result.error)
//                }
//            }
//        })
//
        viewModel.getNsfwLiveData.observe(this, Observer {
            nsfwButton.isActivated = it
        })
//
//        viewModel.getCommunityLiveData.observe(this, Observer {
//            if (it != null)
//                setupCommunity(it)
//        })
//
//        viewModel.getPostToEditLiveData.observe(this, Observer {
//            if (it != null)
//                setupPostToEdit(it)
//        })
//
//        viewModel.getAttachedImageLiveData.observe(this, Observer {
//            when {
//                it.localUri != null -> loadLocalAttachmentImage(it.localUri)
//                it.remoteUrl != null -> loadRemoteAttachmentImage(it.remoteUrl)
//                else -> clearUserPickedImage()
//            }
//        })
//
//        viewModel.getFileUploadingStateLiveData.asEvent().observe(this, Observer { event ->
//            event.getIfNotHandled()?.let { result ->
//                when (result) {
//                    is QueryResult.Loading -> onPostLoading()
//                    is QueryResult.Error -> onPostError(result.error)
//                }
//            }
//        })
    }

    private fun onPostError(error: Throwable) {
        val errorMsg = when (error) {
            is AppError.NotEnoughPowerError -> R.string.not_enough_power
            is AppError.RequestTimeOutException -> R.string.request_timeout_error
            else -> R.string.unknown_error
        }
        NotificationDialog.newInstance(getString(errorMsg)).show(requireFragmentManager(), "create error")
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

//    private fun onEmbedResult(model: LinkEmbedModel) {
    // todo [AS] see it later

//        previewSummary.text = model.summary
//        previewProvider.text = model.provider
//        previewSummary.visibility = if (model.summary.isBlank()) View.GONE else View.VISIBLE
//        previewProvider.visibility = if (model.provider.isBlank()) View.GONE else View.VISIBLE
//        previewDescriptionLayout.visibility =
//            if (model.summary.isBlank() && model.provider.isBlank()) View.GONE else View.VISIBLE
//        linkPreviewImageClear.visibility = View.GONE
//
//        if (model.embedHtml.isNotBlank()) {
//            loadEmbeddedHtml(model)
//        } else {
//            if (model.thumbnailImageUrl.isNotBlank()) {
//                loadThumbnail(model)
//            }
//        }
//    }

    override fun getInitialImageSource() = getArgs().initialImageSource

    override fun onImagePicked(uri: Uri) {
        editorWidget.insertImage(uri, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST) {
            when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY -> pickGalleryPhoto()
                ImagePickerDialog.RESULT_CAMERA -> takeCameraPhoto()
            }
        }
    }

//    private fun loadLocalAttachmentImage(uri: Uri) {
    // todo [AS] see it later

//        linkPreviewWebView.visibility = View.GONE
//        linkPreviewImageViewLayout.visibility = View.VISIBLE
//        linkPreviewImageClear.visibility = View.VISIBLE
//        previewDescriptionLayout.visibility = View.GONE
//        showPreviewLayout()
//
//
//        Glide.with(requireContext())
//            .load(uri)
//            .fitCenter()
//            .into(linkPreviewImageView)
//    }

//    private fun loadRemoteAttachmentImage(url: String) {
    // todo [AS] see it later

//        linkPreviewWebView.visibility = View.GONE
//        linkPreviewImageViewLayout.visibility = View.VISIBLE
//        linkPreviewImageClear.visibility = View.VISIBLE
//        previewDescriptionLayout.visibility = View.GONE
//        showPreviewLayout()
//
//
//        Glide.with(requireContext())
//            .load(url)
//            .fitCenter()
//            .into(linkPreviewImageView)
//    }

//    private fun clearUserPickedImage() {
    // todo [AS] see it later

//        hidePreviewLayout()
//
//        Glide.with(requireContext())
//            .load(0)
//            .fitCenter()
//            .into(linkPreviewImageView)
//    }

//    private fun loadThumbnail(model: LinkEmbedModel) {
    // todo [AS] see it later

//        linkPreviewWebView.visibility = View.GONE
//        linkPreviewImageViewLayout.visibility = View.VISIBLE
//        showPreviewLayout()
//
//        Glide.with(requireContext())
//            .load(model.thumbnailImageUrl)
//            .fitCenter()
//            .into(linkPreviewImageView)
//    }

//    private fun loadEmbeddedHtml(model: LinkEmbedModel) {
    // todo [AS] see it later

//        showPreviewLayout()
//        linkPreviewWebView.visibility = View.VISIBLE
//        linkPreviewImageViewLayout.visibility = View.GONE
//        linkPreviewWebView.loadDataWithBaseURL(model.url, model.embedHtml, "text/html", "UTF-8", null)
//    }

//    private fun onEmbedError() {
    // todo [AS] see it later

//        hidePreviewLayout()
//        linkPreviewProgress.visibility = View.GONE
//        Toast.makeText(requireContext(), "Embedded content error", Toast.LENGTH_SHORT).show()
//    }

//    private fun onEmbedLoading() {
    // todo [AS] see it later

//        linkPreviewProgress.visibility = View.VISIBLE
//        linkPreviewWebView.visibility = View.GONE
//        linkPreviewImageViewLayout.visibility = View.GONE
//    }

//    private fun hidePreviewLayout() {
    // todo [AS] see it later

//        linkPreviewLayout.visibility = View.GONE
//    }

//    private fun showPreviewLayout() {
    // todo [AS] see it later

//        linkPreviewLayout.visibility = View.VISIBLE
//        linkPreviewProgress.visibility = View.GONE
//    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditorPageViewModel::class.java)
    }

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

//    override fun onPause() {
    // todo [AS] see it later

//        linkPreviewWebView.onPause()
//        super.onPause()
//    }

//    override fun onResume() {
    // todo [AS] see it later

//        linkPreviewWebView.onResume()
//        super.onResume()
//    }

    override fun onImagePickingCancel() {
        //noop
    }
}
