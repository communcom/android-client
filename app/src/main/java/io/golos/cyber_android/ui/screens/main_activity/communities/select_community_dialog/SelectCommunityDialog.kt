package io.golos.cyber_android.ui.screens.main_activity.communities.select_community_dialog

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
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog.SelectCommunityDialogComponent
import io.golos.cyber_android.databinding.FragmentCommunitiesSelectDialogBinding
import io.golos.cyber_android.ui.common.extensions.setTextChangeListener
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import kotlinx.android.synthetic.main.fragment_communities_select_dialog.*
import javax.inject.Inject

class SelectCommunityDialog : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(uiHelper: UIHelper, someViewInWindow: View): SelectCommunityDialog {
            uiHelper.setSoftKeyboardVisibility(someViewInWindow, false)
            return SelectCommunityDialog()
        }
    }

    private lateinit var viewModel: SelectCommunityDialogViewModel

    private lateinit var binding: FragmentCommunitiesSelectDialogBinding

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    @Inject
    internal lateinit var uiHelper: UIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.SelectCommunityDialog)

        App.injections.get<SelectCommunityDialogComponent>().inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SelectCommunityDialogViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(viewModel) {
            searchResultVisibility.observe({viewLifecycleOwner.lifecycle}) { updateSearchResultVisibility(it) }

            items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
            searchResultItems.observe({viewLifecycleOwner.lifecycle}) { updateSearchList(it) }

            isScrollEnabled.observe({viewLifecycleOwner.lifecycle}) { setScrollState(it) }
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

        communitiesList.setOnScrollListener { viewModel.onScroll(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<SelectCommunityDialogComponent>()
    }

    private fun updateSearchResultVisibility(isVisible: Boolean) {
        communitiesList.visibility = if(isVisible) View.INVISIBLE else View.VISIBLE
        communitiesSearchList.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
    }
    private fun processViewCommand(command: ViewCommand) {
        when(command) {
            is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
        }
    }

    private fun updateList(data: List<ListItem>) = communitiesList.updateList(data, viewModel)

    private fun updateSearchList(data: List<ListItem>) = communitiesSearchList.updateList(data, viewModel)

    private fun setScrollState(isScrollEnabled: Boolean) = communitiesList.setScrollState(isScrollEnabled)
}