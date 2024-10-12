package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.databinding.FragmentRateDialogBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.ReviewViewModel
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RateDialog : BottomSheetDialogFragment() {

    lateinit var binding: FragmentRateDialogBinding
    private val reviewsViewModel by viewModels<ReviewViewModel>()

    @Inject
    lateinit var appDatastore: AppDatastore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentRateDialogBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            gem = arguments?.getParcelable("gem")


            button7.setOnClickListener {
                if (editTextText5.text.isEmpty())
                    requireContext().showToast("Cant submit empty review")
                else {

                    CoroutineScope(Dispatchers.Main).launch {
                        val review = Review(
                            reviewId = null,
                            userId = appDatastore.userId.first()!!,
                            gemId = gem!!.gemId,
                            reviewText = editTextText5.text.toString()
                        )

                        println("review $review")

                        reviewsViewModel.addReview(review) { reviewResource ->
                            when (reviewResource) {
                                is Resource.Error -> requireContext().showToast("Failed to add review, ${reviewResource.message}")
                                is Resource.Loading -> Unit
                                is Resource.Success -> {
                                    requireContext().showToast(" ✔ reviewed")
                                    dismissAllowingStateLoss()
                                }
                            }
                        }

                    }

                }
            }

        }
    }
}