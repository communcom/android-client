package io.golos.cyber_android.ui.shared_fragments.editor.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment.EditorPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentEditorPageBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view.SelectCommunityDialog
import io.golos.cyber_android.ui.screens.profile.edit.ImagePickerFragmentBase
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkType
import io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.one_text_line.OneTextLineDialog
import io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.text_and_link.TextAndLinkDialog
import io.golos.cyber_android.ui.shared_fragments.editor.view.post_to_editor_loader.PostToEditorLoader
import io.golos.cyber_android.ui.shared_fragments.editor.view_model.EditorPageViewModel
import io.golos.cyber_android.ui.shared_fragments.post.view.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.utils.PostConstants
import io.golos.cyber_android.ui.common.utils.TextWatcherBase
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.*
import io.golos.data.errors.AppError
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.use_cases.post.editor_output.EmbedType
import io.golos.posts_editor.dialogs.selectColor.SelectColorDialog
import io.golos.posts_editor.dto.EditorAction
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

    private lateinit var binding: FragmentEditorPageBinding

    private lateinit var viewModel: EditorPageViewModel

    @Inject
    lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = getArgs()
        App.injections.get<EditorPageFragmentComponent>(args.community, args.postToEdit).inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditorPageViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<EditorPageFragmentComponent>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        observeViewModel()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editor_page, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupView()
    }

    private fun setupView() {
        // Add empty line to the editor for a new post
        if(!viewModel.isInEditMode) {
            editorWidget.insertEmptyParagraph()
        }

        close.setOnClickListener { activity?.finish() }

        postButton.setOnClickListener {
            viewModel.post(editorWidget.getMetadata())
        }

        leaderName.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onTitleChanged(s.toString())
            }
        })

        // Show followers selection dialog
        postCommunity.setOnShowCommunitiesClickListener {
            SelectCommunityDialog.newInstance(uiHelper, postCommunity) {
                community -> community?.let { viewModel.setCommunity(it) }
            }
            .show(requireFragmentManager(), "followers")
        }

        leaderName.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        leaderName.filters = arrayOf(InputFilter.LengthFilter(PostConstants.MAX_POST_TITLE_LENGTH))

        setupEditorToolButtons()
    }

    private fun setupEditorToolButtons() {
        nsfwButton.setOnClickListener {
            viewModel.switchNSFW()
        }

        val possibleEditorActions = editorWidget.getPossibleActions()

        possibleEditorActions.forEach { possibleAction ->
            when(possibleAction) {
                EditorAction.TEXT_BOLD -> {
                    boldButton.visibility = View.VISIBLE
                    boldButton.setOnClickListener { editorWidget.updateTextStyle(TextStyle.BOLD) }
                }
                EditorAction.TEXT_ITALIC -> {
                    italicButton.visibility = View.VISIBLE
                    italicButton.setOnClickListener { editorWidget.updateTextStyle(TextStyle.ITALIC) }
                }
                EditorAction.TEXT_COLOR -> {
                    textColorButton.visibility = View.VISIBLE
                    textColorButton.setOnClickListener {
                        SelectColorDialog(this.requireContext(), MaterialColor.BLACK, R.string.select_text_color, R.string.ok, R.string.cancel) { selectedColor ->
                            selectedColor?.let { editorWidget.updateTextColor(it) }
                        }
                        .show()
                    }
                }
                EditorAction.TAG -> {
                    tagButton.visibility = View.VISIBLE
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
                }
                EditorAction.MENTION -> {
                    mentionButton.visibility = View.VISIBLE
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
                }
                EditorAction.LINK -> {
                    linkInTextButton.visibility = View.VISIBLE
                    linkInTextButton.setOnClickListener {
                        val oldLink = editorWidget.tryGetLinkInTextInfo()
                        TextAndLinkDialog(requireContext(), oldLink?.text ?: "", oldLink?.uri?.toString() ?: "", R.string.enter_link) { text, uri ->
                            if(text != null && uri != null) {
                                viewModel.checkLinkInText(oldLink != null, text, uri)
                            }
                        }
                        .show()
                    }
                }
                EditorAction.LOCAL_IMAGE -> {
                    photoButton.visibility = View.VISIBLE
                    photoButton.setOnClickListener {
                        ImagePickerDialog.newInstance(ImagePickerDialog.Target.EDITOR_PAGE).apply {
                            setTargetFragment(this@EditorPageFragment, GALLERY_REQUEST)
                        }
                        .show(requireFragmentManager(), "cover")
                    }
                }
            }
        }

        if(possibleEditorActions.contains(EditorAction.LOCAL_IMAGE)) {
            viewModel.setEmbedCount(editorWidget.getEmbedCount())
            editorWidget.setOnEmbedAddedOrRemovedListener { isAdded -> viewModel.processEmbedAddedOrRemoved(isAdded)  }
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
        }

        editorWidget.setOnLinkWasPastedListener { viewModel.validatePastedLink(it) }
    }

    private fun observeViewModel() {
        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)

                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)

                is NavigateToMainScreenCommand -> activity?.finish()

                is InsertExternalLinkViewCommand ->
                    with(command.linkInfo) {
                        editorWidget.insertEmbed(type.mapToEmbedType(), sourceUrl, thumbnailUrl, description)
                    }

                is UpdateLinkInTextViewCommand ->
                    with(command) {
                        if(isEdit) {
                            editorWidget.editLinkInText(command.linkInfo)
                        } else {
                            editorWidget.insertLinkInText(command.linkInfo)
                        }
                    }

                is PostErrorViewCommand -> onPostError(command.result)
                is PostCreatedViewCommand -> onPostResult(command.result)

                is PastedLinkIsValidViewCommand -> editorWidget.pastedLinkIsValid(command.uri)

                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })

        viewModel.isEmbedButtonsEnabled.observe(this, Observer {
            photoButton.isEnabled = it
        })

        viewModel.getNsfwLiveData.observe(this, Observer {
            nsfwButton.isActivated = it
        })

        viewModel.editingPost.observe(this, Observer {
            it?.let {
                val parsedPost = it.content.body.postBlock

                    toolbarTitle.setText(R.string.edit_post)
                    leaderName.setText(parsedPost.title)
                    PostToEditorLoader.load(editorWidget, parsedPost)

                    nsfwButton.isActivated = it.content.tags.contains(TagModel("nsfw"))
            }
        })
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

    override fun getInitialImageSource() = getArgs().initialImageSource

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    override fun onImagePicked(uri: Uri) = editorWidget.insertEmbed(EmbedType.LOCAL_IMAGE, uri, uri, null)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST) {
            when (resultCode) {
                ImagePickerDialog.RESULT_GALLERY -> pickGalleryPhoto()
                ImagePickerDialog.RESULT_CAMERA -> takeCameraPhoto()
            }
        }
    }

    override fun onImagePickingCancel() {
        // do nothing
    }

    private fun ExternalLinkType.mapToEmbedType(): EmbedType =
        when(this) {
            ExternalLinkType.IMAGE -> EmbedType.EXTERNAL_IMAGE
            ExternalLinkType.WEBSITE -> EmbedType.EXTERNAL_WEBSITE
            ExternalLinkType.VIDEO -> EmbedType.EXTERNAL_VIDEO
        }
}
