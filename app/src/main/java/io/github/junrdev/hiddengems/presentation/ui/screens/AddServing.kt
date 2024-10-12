package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.databinding.FragmentAddServingBinding
import io.github.junrdev.hiddengems.presentation.viewmodel.ServingsViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource

@AndroidEntryPoint
class AddServing : BottomSheetDialogFragment() {


    lateinit var binding: FragmentAddServingBinding
    private var serving = Serving(null, null, null, null, null)
    private val servingsViewModel by viewModels<ServingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAddServingBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            button3.setOnClickListener {
                val bundle = bundleOf()
                val name = editTextText4.text.toString().trim()

                if (name.isNotEmpty()) {
                    val updated = serving.copy(name = name)

                    servingsViewModel.addServing(updated) { addedResource ->
                        when (addedResource) {
                            is Resource.Error -> {
                                Snackbar.make(
                                    requireView(),
                                    "Failed to add: ${addedResource.message}.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.Loading -> {
                                Snackbar.make(
                                    requireView(),
                                    "Saving, please wait...",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.Success -> {
                                // Add the updated serving (with new ID) to the bundle
                                bundle.putParcelable(
                                    Constant.serving,
                                    updated.copy(id = addedResource.data)
                                )
                                this@AddServing.setFragmentResult(Constant.serving, bundle)
                                dismiss()
                            }
                        }
                    }
                }
            }


        }
    }


    companion object {
        fun newInstance(): AddServing {
            val args = Bundle()
                .apply { }
            val fragment = AddServing()
            fragment.arguments = args
            return fragment
        }
    }
}