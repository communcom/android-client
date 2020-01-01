package io.golos.cyber_android.ui.dialogs.sort

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.widgets.sorting.SortingType
import io.golos.cyber_android.ui.shared.widgets.sorting.TimeFilter
import io.golos.cyber_android.ui.shared.widgets.sorting.TrendingSort
import kotlinx.android.synthetic.main.dialog_sorting_type.*

class SortingTypeDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_sorting_type,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sortsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        @Suppress("UNCHECKED_CAST")
        val sorts = arguments?.getSerializable("sorts") as Array<SortingType>
        sortsList.adapter = SortsAdapter(sorts) { selectedType ->
            targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, Intent().apply {
                if (selectedType is TrendingSort)
                    putExtra(RESULT_TAG, selectedType)
                if (selectedType is TimeFilter)
                    putExtra(RESULT_TAG, selectedType)
            })
            dismiss()
        }
    }

    companion object {

        const val RESULT_TAG = "result_sorting"

        fun newInstance(values: Array<SortingType>): SortingTypeDialogFragment {
            return SortingTypeDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("sorts", values)
                }
            }
        }
    }
}