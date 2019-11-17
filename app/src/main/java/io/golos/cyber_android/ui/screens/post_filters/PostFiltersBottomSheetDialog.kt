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
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.dialog_post_filters.*

class PostFiltersBottomSheetDialog : DialogBaseMVVM<DialogPostFiltersBinding, PostFiltersViewModel>() {

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
        }
    }

    private fun observeViewModel() {
        viewModel.updateTimeFilter.observe(this, Observer {
            removeUpdateTimeFiltersListeners()
            setUpdateTimeFilter(it)
            addUpdateTimeFiltersListeners()
        })

        viewModel.periodTimeFilter.observe(this, Observer {
            removePeriodTimeListeners()
            setPeriodTimeFilter(it)
            addPeriodTimeFiltersListeners()
        })
    }

    private fun setUpdateTimeFilter(filter: PostFiltersHolder.UpdateTimeFilter) {
        when (filter) {
            PostFiltersHolder.UpdateTimeFilter.HOT -> {
                cbHot.isChecked = true
                cbNew.isChecked = false
                cbPopular.isChecked = false
            }
            PostFiltersHolder.UpdateTimeFilter.NEW -> {
                cbHot.isChecked = false
                cbNew.isChecked = true
                cbPopular.isChecked = false
            }
            PostFiltersHolder.UpdateTimeFilter.POPULAR -> {
                cbHot.isChecked = false
                cbNew.isChecked = false
                cbPopular.isChecked = true
            }
        }
    }

    private fun addUpdateTimeFiltersListeners() {
        cbHot.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersHolder.UpdateTimeFilter.HOT)
            }
        }

        cbNew.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersHolder.UpdateTimeFilter.NEW)
            }
        }

        cbPopular.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changeUpdateTimeFilter(PostFiltersHolder.UpdateTimeFilter.POPULAR)
            }
        }
    }

    private fun removeUpdateTimeFiltersListeners() {
        cbHot.setOnCheckedChangeListener(null)
        cbNew.setOnCheckedChangeListener(null)
        cbPopular.setOnCheckedChangeListener(null)
    }

    private fun setPeriodTimeFilter(filter: PostFiltersHolder.PeriodTimeFilter) {
        when (filter) {
            PostFiltersHolder.PeriodTimeFilter.PAST_24_HOURS -> {
                cb24hr.isChecked = true
                cbWeek.isChecked = false
                cbMonth.isChecked = false
                cbAll.isChecked = false
            }
            PostFiltersHolder.PeriodTimeFilter.PAST_WEEK -> {
                cb24hr.isChecked = false
                cbWeek.isChecked = true
                cbMonth.isChecked = false
                cbAll.isChecked = false
            }
            PostFiltersHolder.PeriodTimeFilter.PAST_MONTH -> {
                cb24hr.isChecked = false
                cbWeek.isChecked = false
                cbMonth.isChecked = true
                cbAll.isChecked = false
            }
            PostFiltersHolder.PeriodTimeFilter.ALL -> {
                cb24hr.isChecked = false
                cbWeek.isChecked = false
                cbMonth.isChecked = false
                cbAll.isChecked = true
            }
        }
    }

    private fun addPeriodTimeFiltersListeners() {
        cb24hr.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersHolder.PeriodTimeFilter.PAST_24_HOURS)
            }
        }
        cbWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersHolder.PeriodTimeFilter.PAST_WEEK)
            }
        }
        cbMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersHolder.PeriodTimeFilter.PAST_MONTH)
            }
        }
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.changePeriodTimeFilter(PostFiltersHolder.PeriodTimeFilter.ALL)
            }
        }
    }

    private fun removePeriodTimeListeners() {
        cb24hr.setOnCheckedChangeListener(null)
        cbWeek.setOnCheckedChangeListener(null)
        cbMonth.setOnCheckedChangeListener(null)
        cbAll.setOnCheckedChangeListener(null)
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