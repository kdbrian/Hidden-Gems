package io.github.junrdev.hiddengems.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.databinding.FragmentAddGemBinding
import io.github.junrdev.hiddengems.presentation.adapter.AddGemImagesAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.util.Constant

@AndroidEntryPoint
class AddGem : Fragment() {


    lateinit var binding: FragmentAddGemBinding
    private val images = mutableListOf<Uri>()
    private val servings = mutableListOf<Serving>()
    private lateinit var addedimagesadapter: AddGemImagesAdapter
    private lateinit var addedservingsadapter: ServingListAdapter


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission is granted, launch the image picker
            launchImagePicker()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                requireContext(),
                "Permission Denied to access storage",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (images.size == 4) {
                Toast.makeText(requireContext(), "Image limit reached", Toast.LENGTH_SHORT).show()
            } else {
                images.add(it)
                addedimagesadapter.notifyItemInserted(images.size)
            }

            //toggle visibility
            binding.apply {
                if (addGemImagesList.visibility == View.GONE && images.isNotEmpty()) {
                    loadingAddPlaceImages.apply { stopShimmer(); visibility = View.GONE }
                    addGemImagesList.visibility = View.VISIBLE
                } else if (images.isEmpty() && addGemImagesList.visibility == View.VISIBLE) {
                    addGemImagesList.visibility = View.GONE
                    loadingAddPlaceImages.apply { startShimmer(); visibility = View.VISIBLE }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAddGemBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar3)
        binding.apply {
            addedimagesadapter = AddGemImagesAdapter(images)
            addedservingsadapter = ServingListAdapter(servings)
            addGemImagesList.adapter = addedimagesadapter
            gemFeatures.adapter = addedservingsadapter

            toolbar3.setNavigationOnClickListener { findNavController().navigateUp() }

            textView23.setOnClickListener {
                checkPermissionAndPickImage()
            }


            setFragmentResultListener(Constant.serving) { _, bundle ->
                val serving = bundle.getParcelable<Serving>(Constant.serving)
                serving?.let { servings.add(it); addedservingsadapter.notifyItemInserted(servings.size) }
                if (gemFeatures.visibility == View.GONE && servings.isNotEmpty()) {
                    loadingFeaturesAdd.visibility = View.GONE
                    gemFeatures.visibility = View.VISIBLE
                } else if (gemFeatures.visibility == View.GONE && servings.isEmpty()) {
                    loadingFeaturesAdd.visibility = View.VISIBLE
                    gemFeatures.visibility = View.GONE
                }
            }

            textView21.setOnClickListener {
                findNavController().navigate(R.id.action_addGem_to_addServing)
            }
        }
    }


    private fun checkPermissionAndPickImage() {
        // Check if permission is granted (READ_EXTERNAL_STORAGE or READ_MEDIA_IMAGES depending on Android version)
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES // For Android 13 (Tiramisu) and above
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE // For Android 12 and below
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, launch the image picker
                launchImagePicker()
            }

            else -> {
                // Request the permission
                permissionLauncher.launch(permission)
            }
        }
    }

    // Function to launch the image picker
    private fun launchImagePicker() {
        // Opens the system image picker
        imagePickerLauncher.launch("image/*")
    }


}