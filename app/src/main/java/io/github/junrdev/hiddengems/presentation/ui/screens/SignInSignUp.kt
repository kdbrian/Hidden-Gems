package io.github.junrdev.hiddengems.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.BuildConfig
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.databinding.FragmentSignInSignUpBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.LoadingDialog
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

            //github login
            imageView5.setOnClickListener {
                initiateGithubLogin()
            }

            button2.setOnClickListener {

                if (checkFields()) {

                    CoroutineScope(Dispatchers.Main).launch {
                        val dialog = LoadingDialog.newInstance("Please wait")
                        val accountDto = AccountDto(
                            email = editTextTextEmailAddress.text.toString(),
                            password = editTextTextPassword.text.toString()
                        )

                        //if its not first time -> (login) otherwise (signup)

                        if (!checkBox.isChecked) {
                            usersViewModel.loginUser(accountDto) { userResource ->

                                println("res ${userResource.data}")

                                when (userResource) {

                                    is Resource.Error -> {
                                        dialog.dismiss()
                                        requireContext().showToast(userResource.message.toString())
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

                        } else {
                            usersViewModel.signUpUser(
                                accountDto
                            ) { createAccountResource ->
                                when (createAccountResource) {

                                    is Resource.Error -> {
                                        dialog.dismiss()
                                        requireContext().showToast(
                                            createAccountResource.message ?: "Failed retry again."
                                        )
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
                                            appDatastore.saveFirstTime()
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

        }
    }

    private fun initiateGithubLogin() {
        val clientId = BuildConfig.clientID

        val aouthurl =
            "https://github.com/login/oauth/authorize?client_id=$clientId&scope=read:user,user:email" +
                    "&redirect_uri=hiddengems://hiddengemsghub0auth"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(aouthurl))
        startActivity(intent)
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