package io.golos.cyber_android.ui.screens.post_filters

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters.PostFiltersFragmentComponent
import io.golos.cyber_android.databinding.DialogPostFiltersBinding
import io.golos.cyber_android.ui.common.mvvm.DialogBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ApplyPostFiltersCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.dialog_post_filters.*

class PostFiltersBottomSheetDialog : DialogBaseMVVM<DialogPostFiltersBinding, PostFiltersViewModel>() {

    private var filterChangeListener: FilterChangeListener? = null

    override fun provideViewModelType(): Class<PostFiltersViewModel> = PostFiltersViewModel::class.java

    override fun layoutResId(): Int = R.layout.dialog_post_filters

    override fun inject() {
        App.injections
            .get<PostFiltersFragmentComponent>()
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<PostFiltersFragmentComponent>()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun linkViewModel(binding: DialogPostFiltersBinding, viewModel: PostFiltersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addUpdateTimeFiltersListeners()
        addPeriodTimeFiltersListeners()
        observeViewModel()
        btnNext.setOnClickListener {
            viewModel.onNextClicked()
        }
        ivClose.setOnClickListener {
            viewModel.onClosedClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        changeBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun onResume() {
        super.onResume()
        setBottomSheetBahavioutStateListener()
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is BackCommand -> dismiss()
            is ApplyPostFiltersCommand -> {
                filterChangeListener?.onFiltersChanged(command.postFilters)
                dismiss()
            }
        }
    }

    fun setFiltersApplyListener(listener: FilterChangeListener?) {
        filterChangeListener = listener
    }

    interface FilterChangeListener {

        fun onFiltersChanged(filters: PostFilters)
    }

    private fun observeViewModel() {
        viewModel.updateTimeFilterLiveData.observe(this, Observer {
            removeUpdateTimeFiltersListeners()
            setUpdateTimeFilter(it)
            addUpdateTimeFiltersListeners()
        })

        viewModel.periodTimeFilterLiveData.observe(this, Observer {
            removePeriodTimeListeners()
            setPeriodTimeFilter(it)
            addPeriodTimeFiltersListeners()
        })
    }

    private fun setUpdateTimeFilter(filter: PostFiltersViewModel.UpdateTimeFilter) {
        when (filter) {
            PostFiltersViewModel.UpdateTimeFilter.TOP -> {
                cbTop.isChecked = true
                cbNew.isChecked = false
                cbOld.isChecked = false
            }
            PostFiltersViewModel.UpdateTimeFilter.NEW -> {
                cbNew.isChecked = true
                cbTop.isChecked = false
                cbOld.isChecked = false
            }
            PostFiltersViewModel.UpdateTimeFilter.OLD -> {
                cbOld.isChecked = true
                cbTop.isChecked = false
                cbNew.isChecked = false
            }
        }
    }

    private fun addUpdateTimeFiltersListeners() {
        cbTop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersViewModel.UpdateTimeFilter.TOP)
            }
        }

        cbNew.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersViewModel.UpdateTimeFilter.NEW)
            }
        }

        cbOld.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersViewModel.UpdateTimeFilter.OLD)
            }
        }
    }

    private fun removeUpdateTimeFiltersListeners() {
        cbTop.setOnCheckedChangeListener(null)
        cbNew.setOnCheckedChangeListener(null)
        cbOld.setOnCheckedChangeListener(null)
    }

    private fun setPeriodTimeFilter(filter: PostFiltersViewModel.PeriodTimeFilter) {
        when (filter) {
            PostFiltersViewModel.PeriodTimeFilter.PAST_24_HOURS -> {
                cb24hr.isChecked = true
                cbMonth.isChecked = false
                cbYear.isChecked = false
            }
            PostFiltersViewModel.PeriodTimeFilter.PAST_MONTH -> {
                cbMonth.isChecked = true
                cb24hr.isChecked = false
                cbYear.isChecked = false
            }
            PostFiltersViewModel.PeriodTimeFilter.PAST_YEAR -> {
                cbYear.isChecked = true
                cb24hr.isChecked = false
                cbMonth.isChecked = false
            }
        }
    }

    private fun addPeriodTimeFiltersListeners() {
        cb24hr.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersViewModel.PeriodTimeFilter.PAST_24_HOURS)
            }
        }
        cbMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersViewModel.PeriodTimeFilter.PAST_MONTH)
            }
        }
        cbYear.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersViewModel.PeriodTimeFilter.PAST_YEAR)
            }
        }
    }

    private fun removePeriodTimeListeners() {
        cb24hr.setOnCheckedChangeListener(null)
        cbMonth.setOnCheckedChangeListener(null)
        cbYear.setOnCheckedChangeListener(null)
    }

    override fun getTheme(): Int = R.style.PostFiltersBottomSheet

    private fun getBehaviour(): BottomSheetBehavior<View>? {
        val dialog = dialog
        if (dialog != null) {
            val viewById = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (viewById != null) {
                val bottomSheet = viewById as FrameLayout
                return BottomSheetBehavior.from(bottomSheet)
            }
        }
        return null
    }

    private fun setBottomSheetBahavioutStateListener(){
        val behaviour = getBehaviour()
        behaviour?.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == STATE_HIDDEN){
                    viewModel.onClosedClicked()
                }
            }

        })
    }

    private fun changeBottomSheetBehaviorState(state: Int) {
        val behaviour = getBehaviour()
        behaviour?.state = state
    }
}