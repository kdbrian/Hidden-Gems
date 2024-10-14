package io.github.junrdev.hiddengems.presentation.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.GithubUserAccount
import io.github.junrdev.hiddengems.data.model.FirebaseUserAccount
import io.github.junrdev.hiddengems.databinding.FragmentAccountBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.UsersViewModel
import io.github.junrdev.hiddengems.util.AccountMode
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Account : Fragment() {

    lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var appDatastore: AppDatastore

    @Inject
    lateinit var auth: FirebaseAuth

    private val usersViewModel by viewModels<UsersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return FragmentAccountBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.materialToolbar)

        binding.apply {
            materialToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            CoroutineScope(Dispatchers.Main).launch {

                //check user account mode
                val logginMode = appDatastore.logginMode.first()

                println("mode $logginMode")

                val userId = appDatastore.userId.first().orEmpty()
                val accountMode = appDatastore.logginMode.first()

                CoroutineScope(Dispatchers.Main).launch {
                    if (userId.isNotEmpty()) {
                        usersViewModel.loadUserDetails(userId, accountMode) { userResource ->
                            when (userResource) {
                                is Resource.Error -> {
                                    loadingProfile.stopShimmer()
                                    requireContext().showToast("failed to load account ${userResource.message}")
                                }

                                is Resource.Loading -> {
                                    loadingProfile.apply {
                                        visibility = View.VISIBLE; startShimmer()
                                    }
                                    profileGroup.visibility = View.GONE
                                }

                                is Resource.Success -> {
                                    CoroutineScope(Dispatchers.Main).launch {

                                        val appUser = userResource.data

                                        delay(200)
                                        loadingProfile.apply {
                                            stopShimmer()
                                            visibility = View.GONE;
                                        }
                                        profileGroup.visibility = View.VISIBLE

                                        appUser?.let {
                                            when (it) {
                                                //load account type
                                                is GithubUserAccount -> {
                                                    textView38.visibility = View.GONE
                                                    val ghubUser = it
                                                    println("guser $ghubUser")
                                                    Glide.with(requireContext())
                                                        .load(ghubUser.avatarUrl)
                                                        .centerCrop().into(profilePic)

                                                    textView31.text = "user#${ghubUser.id}"
                                                    textView32.text = ghubUser.username
                                                    println("github user")
                                                }

                                                is FirebaseUserAccount -> {
                                                    appDatastore.refreshVerificationDetails()
                                                    textView38.isChecked =
                                                        appDatastore.isEmailVerified.first()

                                                    if (appDatastore.isEmailVerified.first()) {
                                                        textView38.isEnabled = false
                                                    } else
                                                        textView38.setOnCheckedChangeListener { _, checked ->
                                                            if (checked) {
                                                                textView38.isChecked = false
                                                                auth.currentUser!!.sendEmailVerification()
                                                                    .addOnSuccessListener {
                                                                        requireContext().showToast(
                                                                            "check your mailbox to verify email."
                                                                        ); return@addOnSuccessListener
                                                                    }
                                                                    .addOnFailureListener {
                                                                        requireContext().showToast(
                                                                            "Failed to send email due to ${it.message}"
                                                                        ); return@addOnFailureListener
                                                                    }
                                                            } else
                                                                return@setOnCheckedChangeListener
                                                        }

                                                    profileGroup.visibility = View.VISIBLE
                                                    textView31.text =
                                                        "user#${
                                                            appDatastore.userId.first().toString()
                                                                .substring(0, 5)
                                                        }"
                                                    textView32.text = appDatastore.userEmail.first()
                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        }
                    }
                }

                if (logginMode == AccountMode.NO_LOGIN.mode) {
                    //TODO: consider this case
                    loadingProfile.stopShimmer()
                }


                textView33.isChecked = appDatastore.locationSharing.first()
                textView33.setOnCheckedChangeListener { _, checked ->
                    CoroutineScope(Dispatchers.Main).launch {
                        appDatastore.toggleLocation(checked)
                        updateProfileSettings()
                    }
                }


                textView34.isChecked = appDatastore.rememberUser.first()
                textView34.setOnCheckedChangeListener { _, checked ->
                    CoroutineScope(Dispatchers.Main).launch {
                        appDatastore.toggleRememberMe(checked)
                        updateProfileSettings()
                    }
                }


                openGithubButton.setOnClickListener {
                    val githubUrl = "https://github.com/kdbrian/Hidden-Gems/"
                    openGitHubLink(githubUrl)
                }

            }

            textView35.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    appDatastore.logoutUser()
                    findNavController().navigate(R.id.appnavigation)
                }
            }


        }
    }

    private fun updateProfileSettings() {
        CoroutineScope(Dispatchers.Main).launch {
            val mode = appDatastore.logginMode.first()
            val uid = appDatastore.userId.first()
            val loc = appDatastore.locationSharing.first()
            val rem = appDatastore.rememberUser.first()
            usersViewModel.toggleSetting(
                userId = uid!!,
                type = mode,
                location = loc,
                remember = rem
            )
        }
    }


    private fun openGitHubLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        val packageManager = requireActivity().packageManager
        val githubAppPackage = "com.github.android"
        if (isAppInstalled(githubAppPackage, packageManager)) {
            intent.setPackage(githubAppPackage)
        }
        startActivity(intent)
    }

    private fun isAppInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


}