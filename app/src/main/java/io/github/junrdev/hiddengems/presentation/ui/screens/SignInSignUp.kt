package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.LoadingDialog
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.databinding.FragmentSignInSignUpBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.UsersViewModel
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInSignUp : Fragment() {

    lateinit var binding: FragmentSignInSignUpBinding
    private val usersViewModel by viewModels<UsersViewModel>()

    @Inject
    lateinit var appDatastore: AppDatastore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSignInSignUpBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            button2.setOnClickListener {

                if (checkFields()) {


                    val dialog = LoadingDialog.newInstance("Please wait")

                    usersViewModel.signUpUser(
                        AccountDto(
                            email = editTextTextEmailAddress.text.toString(),
                            password = editTextTextPassword.text.toString()
                        )
                    ) { createAccountResource ->
                        when (createAccountResource) {

                            is Resource.Error -> {
                                dialog.dismiss()
                                requireContext().showToast(createAccountResource.message.toString())
                            }

                            is Resource.Loading -> {
                                requireContext().showToast("Please wait.")
                                dialog
                                    .show(parentFragmentManager, null)
                            }

                            is Resource.Success -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    dialog.dismiss()
                                    appDatastore.loginUser()
                                    findNavController().navigate(R.id.action_signInSignUp_to_homeScreen)
                                    findNavController().popBackStack(
                                        R.id.action_signInSignUp_to_homeScreen,
                                        true
                                    )
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    private fun FragmentSignInSignUpBinding.checkFields(): Boolean {

        if (editTextTextEmailAddress.text.toString().isEmpty()) {
            requireContext().showToast("Provide email adress.")
            return false
        }


        if (editTextTextPassword.text.toString().isEmpty()) {
            requireContext().showToast("password is required.")
            return false
        }

        return true
    }
}