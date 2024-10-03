package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.FragmentOnBoardBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class OnBoard : Fragment() {


    lateinit var binding: FragmentOnBoardBinding

    @Inject
    lateinit var appDatastore: AppDatastore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOnBoardBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {

            button.setOnClickListener {
                findNavController().navigate(R.id.action_onBoard_to_signInSignUp)
            }

            //add no account options
            textView.setOnClickListener {
                findNavController().navigate(R.id.action_onBoard_to_homeScreen)
            }

            CoroutineScope(Dispatchers.Main).launch {
                //check if logged in
                if (appDatastore.isLoggedIn.first()) {
                    findNavController().navigate(R.id.action_onBoard_to_homeScreen)
                }
            }


        }
    }
}