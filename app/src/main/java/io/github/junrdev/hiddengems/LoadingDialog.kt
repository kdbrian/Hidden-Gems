package io.github.junrdev.hiddengems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.databinding.FragmentLoadingDialogBinding

@AndroidEntryPoint
class LoadingDialog : DialogFragment() {
    lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLoadingDialogBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textView29.text = arguments?.getString("message")
        }
    }


    companion object {
        fun newInstance(message: String): LoadingDialog {
            val args = Bundle()
                .apply { putString("message", message) }
            val fragment = LoadingDialog()
            fragment.arguments = args
            return fragment
        }
    }
}