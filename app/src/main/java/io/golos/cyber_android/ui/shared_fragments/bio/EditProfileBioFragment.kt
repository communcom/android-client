package io.golos.cyber_android.ui.shared_fragments.bio

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.TextWatcherBase
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
import kotlinx.android.synthetic.main.edit_bio_fragment.*

class EditProfileBioFragment : FragmentBase() {

    data class Args(
        val user: CyberName,
        val initialBio: String
    )

    companion object {
        fun newInstance(serializedArgs: String) = EditProfileBioFragment().apply {
            arguments = Bundle().apply {
                putString(Tags.ARGS, serializedArgs)
            }
        }
    }

    private lateinit var viewModel: EditProfileBioViewModel

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

        bio.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onBioChanged(s.toString())
            }
        })
        close.setOnClickListener { requireActivity().finish() }

        post.setOnClickListener {
            viewModel.updateBio()
        }

        bio.filters = arrayOf(InputFilter.LengthFilter(EditProfileBioViewModel.MAX_BIO_LENGTH))
        bio.setText(getArgs().initialBio)
        bio.setSelection(bio.text.length)
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            post.isEnabled = it
        })

        viewModel.getBioLengthLiveData.observe(this, Observer { length ->
            counter.text = String.format(
                getString(R.string.bio_length_format), length,
                EditProfileBioViewModel.MAX_BIO_LENGTH
            )
        })

        viewModel.getMetadataUpdateStateLiveData.asEvent().observe(this, Observer { event ->
            event?.getIfNotHandled()?.let {
                when (it) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Error -> onError()
                    is QueryResult.Success -> onSuccess()
                }
            }
        })
    }

    private fun onSuccess() {
        hideLoading()
        requireActivity().finish()
    }

    private fun onError() {
        Toast.makeText(requireContext(), "Error updating bio", Toast.LENGTH_SHORT).show()
        hideLoading()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getViewModelFactoryByCyberName(getArgs().user)
        ).get(EditProfileBioViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!

}
