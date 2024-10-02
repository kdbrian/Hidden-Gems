package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.databinding.FragmentAddServingBinding
import io.github.junrdev.hiddengems.util.Constant

@AndroidEntryPoint
class AddServing : BottomSheetDialogFragment() {


    lateinit var binding: FragmentAddServingBinding
    private var priceranges = listOf<Float>()
    private var serving = Serving(null, null, null, null, null)

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


            toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) {
                    editTextNumberDecimal.visibility = View.GONE
                    priceRange.visibility = View.GONE

                } else {
                    when (checkedId) {
                        R.id.button5 -> {
                            editTextNumberDecimal.visibility = View.VISIBLE
                            priceRange.visibility = View.GONE
                        }

                        R.id.button6 -> {
                            editTextNumberDecimal.visibility = View.GONE
                            priceRange.visibility = View.VISIBLE
                        }
                    }
                }

            }

            priceRange.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) {
                    val values = slider.values
                    priceranges = slider.values
                    println("at ${values[0]} - ${values[1]}")
                }
            }

            button3.setOnClickListener {

                var updated = serving.copy(
                    priceFrom = if (priceranges.isNotEmpty()) priceranges[0].toDouble() else 0.0,
                    priceTo = if (priceranges.isNotEmpty()) priceranges[1].toDouble() else 0.0,
                    price = if (editTextNumberDecimal.text.isNotEmpty()) editTextNumberDecimal.text.toString()
                        .trim().toDouble() else 0.0,
                    name = editTextText4.text.toString().ifEmpty { "unknown" }
                )


                setFragmentResult(Constant.serving, bundleOf(Constant.serving to serving))
                dismiss()
            }
        }
    }
}