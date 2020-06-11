package io.golos.cyber_android.ui.dialogs.select_community_dialog.view

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dialogs.select_community_dialog.di.SelectCommunityDialogComponent
import io.golos.cyber_android.databinding.FragmentCommunitiesSelectDialogBinding
import io.golos.cyber_android.ui.shared.extensions.setTextChangeListener
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.select_community_dialog.dto.CommunitySelected
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view_model.SelectCommunityDialogViewModel
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.dto.CommunityDomain
import io.golos.utils.id.IdUtil
import kotlinx.android.synthetic.main.fragment_communities_select_dialog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SelectCommunityDialog : BottomSheetDialogFragment() {
    companion object {
        /**
         * @param closeAction null if a community was not selected
         */
        fun show(parent: Fragment, closeAction: (CommunityDomain?) -> Unit) {
            SelectCommunityDialog()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "SELECT_COMMUNITY")
        }
    }

    private lateinit var  closeActionListener: (CommunityDomain?) -> Unit

    private var isCommunitySelected = false

    private lateinit var viewModel: SelectCommunityDialogViewModel

    private lateinit var binding: FragmentCommunitiesSelectDialogBinding

    private val injectionKey = IdUtil.generateStringId()

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    @Inject
    internal lateinit var uiHelper: UIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)

        App.injections.get<SelectCommunityDialogComponent>(injectionKey).inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SelectCommunityDialogViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        setupDialog(dialog)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            searchResultVisibility.observe({viewLifecycleOwner.lifecycle}) { updateSearchResultVisibility(it) }

            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
            searchResultItems.observe({viewLifecycleOwner.lifecycle}) { updateSearchList(it) }
        }

        viewModel.command.observe({viewLifecycleOwner.lifecycle}) {
            processViewCommand(it)
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_communities_select_dialog, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchField.setTextChangeListener { viewModel.onSearchStringUpdated(it) }
        ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isCommunitySelected) {
            closeActionListener(null)
        }

        App.injections.release<SelectCommunityDialogComponent>(injectionKey)
    }

    private fun updateSearchResultVisibility(isVisible: Boolean) {
        communitiesList.visibility = if(isVisible) View.INVISIBLE else View.VISIBLE
        communitiesSearchList.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
    }
    private fun processViewCommand(command: ViewCommand) {
        when(command) {
            is ShowMessageResCommand -> uiHelper.showMessage(command.textResId, command.isError)
            is ShowMessageTextCommand -> uiHelper.showMessage(command.text, command.isError)

            is CommunitySelected -> {
                closeActionListener(command.community)
                isCommunitySelected = true
                dismiss()
            }
        }
    }

    private fun updateList(data: List<VersionedListItem>) = communitiesList.updateList(data, viewModel)

    private fun updateSearchList(data: List<VersionedListItem>) = communitiesSearchList.updateList(data, viewModel)

    private fun setupDialog(dialog: Dialog) {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }
}