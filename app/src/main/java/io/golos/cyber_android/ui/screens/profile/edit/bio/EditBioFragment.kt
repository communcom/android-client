package io.golos.cyber_android.ui.screens.profile.edit.bio

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.views.utils.BaseTextWatcher
import kotlinx.android.synthetic.main.edit_bio_fragment.*

class EditBioFragment : Fragment() {

    data class Args(
        val initialBio: String
    )

    companion object {
        fun newInstance(serializedArgs: String) = EditBioFragment().apply {
            arguments = Bundle().apply {
                putString(Tags.ARGS, serializedArgs)
            }
        }
    }

    private lateinit var viewModel: EditBioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_bio_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        bio.addTextChangedListener(object: BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onBioChanged(s.toString())
            }
        })
        close.setOnClickListener { requireActivity().finish() }

        post.setOnClickListener { requireActivity().finish() }

        bio.filters = arrayOf(InputFilter.LengthFilter(EditBioViewModel.MAX_BIO_LENGTH))
        bio.setText(getArgs().initialBio)
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            post.isEnabled = it
        })

        viewModel.getBioLengthLiveData.observe(this, Observer { length ->
            counter.text = String.format(getString(R.string.bio_length_format), length,
                EditBioViewModel.MAX_BIO_LENGTH
            )
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(EditBioViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!

}
