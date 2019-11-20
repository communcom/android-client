package io.golos.cyber_android.ui.shared_fragments.bio

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment.BioFragmentComponent
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.utils.asEvent
import io.golos.cyber_android.ui.utils.TextWatcherBase
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.edit_bio_fragment.*
import javax.inject.Inject

class EditProfileBioFragment : FragmentBase() {

    @Parcelize
    data class Args(
        val userCyberName: String,
        val initialBio: String
    ): Parcelable

    companion object {
        fun newInstance(serializedArgs: Args) = EditProfileBioFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Tags.ARGS, serializedArgs)
            }
        }
    }

    private lateinit var viewModel: EditProfileBioViewModel

    @Inject
    lateinit var viewModelFactory: FragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<BioFragmentComponent>(CyberName(getArgs().userCyberName)).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<BioFragmentComponent>()
    }

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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditProfileBioViewModel::class.java)
    }

    private fun getArgs() = arguments!!.getParcelable<Args>(Tags.ARGS)!!
}
