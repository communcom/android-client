package io.golos.cyber_android.ui.screens.post_edit.fragment.view

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
import io.golos.cyber_android.databinding.FragmentEditorPageBinding
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.ImagePickerDialog
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view.SelectCommunityDialog
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.fragment.di.EditorPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkType
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ShowCloseConfirmationDialogCommand
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.image_picker.ImagePickerFragmentBase
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.post_to_editor_loader.PostToEditorLoader
import io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands.InsertExternalLinkViewCommand
import io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands.PastedLinkIsValidViewCommand
import io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands.PostCreatedViewCommand
import io.golos.cyber_android.ui.screens.post_edit.fragment.view_model.EditorPageViewModel
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.keyboard.KeyboardUtils
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.EmbedType
import io.golos.posts_editor.dialogs.selectColor.SelectColorDialog
import io.golos.posts_editor.dto.EditorAction
import io.golos.posts_editor.utilities.MaterialColor
import io.golos.utils.id.IdUtil
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_editor_page.*
import javax.inject.Inject

class EditorPageFragment : ImagePickerFragmentBase() {
    @Parcelize
    data class Args(
        val contentId: ContentId? = null,
        val initialImageSource: ImageSource = ImageSource.NONE
    ) : Parcelable

    private lateinit var binding: FragmentEditorPageBinding

    private lateinit var viewModel: EditorPageViewModel

    private val injectionKey = IdUtil.generateStringId()

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    @Inject
    internal lateinit var  activityBridge: EditorPageBridgeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = getArgs()
        App.injections.get<EditorPageFragmentComponent>(
            injectionKey,
            args.contentId
        ).inject(this)

        activityBridge.registerOnCloseEditorChecker {
            viewModel.tryToClose(editorWidget.getMetadata(), isCloseButtonAction = false)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditorPageViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()

        activityBridge.registerOnCloseEditorChecker(null)
        App.injections.release<EditorPageFragmentComponent>(injectionKey)
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
        if (!viewModel.isInEditMode) {
            editorWidget.insertEmptyParagraph()
        }

        close.setOnClickListener { viewModel.tryToClose(editorWidget.getMetadata(), isCloseButtonAction = true) }

        postButton.setOnClickListener { viewModel.post(editorWidget.getMetadata()) }

        leaderName.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onTitleChanged(s.toString())
            }
        })

        // Show followers selection dialog
        postCommunity.setOnShowCommunitiesClickListener {
            showDialog {
                SelectCommunityDialog.show(this) { community ->
                    community?.let { viewModel.setCommunity(it) }
                }
            }
        }

        leaderName.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        leaderName.filters = arrayOf(InputFilter.LengthFilter(io.golos.utils.PostConstants.MAX_POST_TITLE_LENGTH))

        setupEditorToolButtons()
    }

    private fun showDialog(showDialogAction: () -> Unit) {
        if(KeyboardUtils.isKeyboardVisible(leaderName)) {
            KeyboardUtils.hideKeyboard(leaderName)
            leaderName.postDelayed({ showDialogAction() }, 300)
        } else {
            showDialogAction()
        }
    }

    private fun setupEditorToolButtons() {
        nsfwButton.setOnClickListener {
            viewModel.switchNSFW()
        }

        val possibleEditorActions = editorWidget.getPossibleActions()
            .filter { editorAction -> editorAction == EditorAction.LOCAL_IMAGE }

        possibleEditorActions.forEach { possibleAction ->
            when (possibleAction) {
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
                        SelectColorDialog(
                            this.requireContext(),
                            MaterialColor.BLACK,
                            R.string.select_text_color,
                            R.string.ok,
                            R.string.cancel
                        ) { selectedColor ->
                            selectedColor?.let { editorWidget.updateTextColor(it) }
                        }
                            .show()
                    }
                }
                EditorAction.LOCAL_IMAGE -> {
                    photoButton.visibility = View.VISIBLE
                    photoButton.setOnClickListener {
                        ImagePickerDialog.show(this) {
                            when(it) {
                                ImagePickerDialog.Result.Camera -> takeCameraPhoto()
                                ImagePickerDialog.Result.Gallery -> pickGalleryPhoto()
                            }
                        }
                    }
                }
            }
        }

        if (possibleEditorActions.contains(EditorAction.LOCAL_IMAGE)) {
            viewModel.setEmbedCount(editorWidget.getEmbedCount())
            editorWidget.setOnEmbedAddedOrRemovedListener { isAdded -> viewModel.processEmbedAddedOrRemoved(isAdded) }
        }

        boldButton.isEnabled = false
        italicButton.isEnabled = false
        textColorButton.isEnabled = false

        editorWidget.setOnSelectionTextChangeListener { isSomeTextSelected ->
            boldButton.isEnabled = isSomeTextSelected
            italicButton.isEnabled = isSomeTextSelected
            textColorButton.isEnabled = isSomeTextSelected

            photoButton.isEnabled = !isSomeTextSelected
        }

        editorWidget.setOnLinkWasPastedListener { viewModel.validatePastedLink(it) }
    }

    private fun observeViewModel() {
        viewModel.command.observe(viewLifecycleOwner, Observer { command ->
            when (command) {
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)

                is ShowMessageResCommand -> uiHelper.showMessage(command.textResId, command.isError)
                is ShowMessageTextCommand -> uiHelper.showMessage(command.text, command.isError)

                is NavigateToMainScreenCommand -> closeEditor()

                is InsertExternalLinkViewCommand ->
                    with(command.linkInfo) {
                        editorWidget.insertEmbed(type.mapToEmbedType(), sourceUrl, thumbnailUrl, description)
                    }

                is PostCreatedViewCommand -> onPostResult(command.contentId)

                is PastedLinkIsValidViewCommand -> editorWidget.pastedLinkIsValid(command.uri)

                is ShowCloseConfirmationDialogCommand -> showCloseConfirmationDialog()

                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })

        viewModel.isEmbedButtonsEnabled.observe(viewLifecycleOwner, Observer {
            photoButton.isEnabled = it
        })

        viewModel.getNsfwLiveData.observe(viewLifecycleOwner, Observer {
            nsfwButton.isActivated = it
        })

        viewModel.editingPost.observe(viewLifecycleOwner, Observer { postDomain ->
            postDomain?.let { post ->
                val parsedPost = post.body

                parsedPost?.let { parsedBody ->
                    toolbarTitle.setText(R.string.edit_post)
                    leaderName.setText(parsedBody.title)
                    PostToEditorLoader.load(editorWidget, parsedPost)
//                    nsfwButton.isActivated = post.tags.contains(TagModel("nsfw")) //todo didn't have in posts
                }

            }
        })
    }

    private fun onPostResult(contentId: ContentId) {
        hideLoading()
        val intent = Intent(Tags.ACTION_EDIT_SUCCESS).apply {
            putExtra(Tags.CONTENT_ID, contentId)
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun getInitialImageSource() = getArgs().initialImageSource

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!

    override fun onImagePicked(uri: Uri) = editorWidget.insertEmbed(EmbedType.LOCAL_IMAGE, uri, uri, null)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            ConfirmationDialog.REQUEST -> {
                if (resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.close()
                }
            }
        }
    }

    override fun onImagePickingCancel() {
        // do nothing
    }

    private fun ExternalLinkType.mapToEmbedType(): EmbedType =
        when (this) {
            ExternalLinkType.IMAGE -> EmbedType.EXTERNAL_IMAGE
            ExternalLinkType.WEBSITE -> EmbedType.EXTERNAL_WEBSITE
            ExternalLinkType.VIDEO -> EmbedType.EXTERNAL_VIDEO
        }

    private fun closeEditor() {
        activity?.apply {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun showCloseConfirmationDialog() =
        ConfirmationDialog.newInstance(R.string.close_editor_confirmation, this@EditorPageFragment)
            .show(requireFragmentManager(), "menu")
}

