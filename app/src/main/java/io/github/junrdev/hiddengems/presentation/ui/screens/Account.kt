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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.FragmentAccountBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Account : Fragment() {

    lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var appDatastore: AppDatastore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAccountBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.materialToolbar)

        binding.apply {
            materialToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            CoroutineScope(Dispatchers.Main).launch {
                textView31.text = "user#${appDatastore.userId.first().toString().substring(0, 5)}"
                textView32.text = appDatastore.userEmail.first()


                textView33.isChecked = appDatastore.locationSharing.first()
                textView33.setOnCheckedChangeListener { _, checked ->
                    launch {
                        appDatastore.toggleLocation(checked)
                    }
                }


                textView34.isChecked = appDatastore.locationSharing.first()
                textView34.setOnCheckedChangeListener { _, checked ->
                    launch {
                        appDatastore.toggleRememberMe(checked)
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