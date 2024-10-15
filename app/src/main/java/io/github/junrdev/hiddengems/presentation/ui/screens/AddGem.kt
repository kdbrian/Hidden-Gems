package io.github.junrdev.hiddengems.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.GemDto
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.databinding.FragmentAddGemBinding
import io.github.junrdev.hiddengems.presentation.adapter.AddGemImagesAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.LoadingDialog
import io.github.junrdev.hiddengems.presentation.ui.getAdress
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.ui.toStringJson
import io.github.junrdev.hiddengems.presentation.viewmodel.GemsViewModel
import io.github.junrdev.hiddengems.presentation.viewmodel.ServingsViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddGem : Fragment() {


    lateinit var binding: FragmentAddGemBinding
    private val images = mutableListOf<Uri>()
    private val servings = mutableListOf<Serving>()
    private val servingsIds = mutableListOf<String>()
    private lateinit var addedimagesadapter: AddGemImagesAdapter
    private lateinit var addedservingsadapter: ServingListAdapter

    @Inject
    lateinit var appDatastore: AppDatastore


    private val gemsViewModel by viewModels<GemsViewModel>()
    private val servingsViewModel by viewModels<ServingsViewModel>()

    private var allgemsservings: List<Serving> = emptyList()
    private val prefilledIds = mutableListOf<Int>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latLng: LatLng? = null

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkLocationPermissionAndGetCurrentLocation()
        } else {
            // Handle permission denial if needed
            requireContext().showToast("Location access is required.")
            binding.switch1.isChecked = false
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchImagePicker()
        } else {
            requireContext().showToast("Permission Denied to access storage")
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            images.add(it)
            addedimagesadapter.notifyItemInserted(images.size)
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
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.apply {

            addedimagesadapter = AddGemImagesAdapter(images)
            addedservingsadapter = ServingListAdapter(servings)
            addGemImagesList.adapter = addedimagesadapter
            gemFeatures.adapter = addedservingsadapter

            toolbar3.setNavigationOnClickListener { findNavController().navigateUp() }

            textView23.setOnClickListener {
                if (images.size == 4) {
                    requireContext().showToast("Image limit reached")
                } else
                    checkPermissionAndPickImage()
            }

            servingsViewModel.servings.observe(viewLifecycleOwner) { servingResource ->
                when (servingResource) {
                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        loadingPrefilledFeaturesAdd.visibility = View.GONE
                        val servings = servingResource.data!!
                        allgemsservings = servings

                        for (s in servings.map { it.name }) {
                            servingsChips.addView(
                                Chip(
                                    requireContext(),
                                    null,
                                    com.google.android.material.R.attr.chipStyle
                                )
                                    .apply {
                                        text = s
                                        isCheckable = true
                                        checkedIcon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.round_check_circle_24
                                        )
                                        requireContext().run {
                                            chipBackgroundColor =
                                                getColorStateList(R.color.davysGrey)
                                            setTextColor(
                                                ContextCompat.getColor(
                                                    this,
                                                    R.color.aliceBlue
                                                )
                                            )

                                            setOnCheckedChangeListener { _, checked ->
                                                when (checked) {
                                                    true -> {
                                                        chipBackgroundColor =
                                                            getColorStateList(R.color.chillRed)
                                                        setTextColor(
                                                            ContextCompat.getColor(
                                                                this,
                                                                R.color.aliceBlue
                                                            )
                                                        )

                                                        chipIcon = ContextCompat.getDrawable(
                                                            this,
                                                            R.drawable.round_check_circle_24
                                                        )
                                                        isChipIconVisible = true


                                                    }

                                                    false -> {
                                                        isChipIconVisible = false
                                                        chipBackgroundColor =
                                                            getColorStateList(R.color.davysGrey)
                                                        setTextColor(
                                                            ContextCompat.getColor(
                                                                this,
                                                                R.color.aliceBlue
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            chipStartPadding = 24f
                                            chipEndPadding = 24f


                                        }
                                    }
                            )
                        }

                        servingsChips.setOnCheckedStateChangeListener { group, checkedIds ->
                            prefilledIds.clear()
                            prefilledIds.addAll(checkedIds)
                        }

                    }
                }
            }


            setFragmentResultListener(Constant.serving) { _, bundle ->
                val serving = bundle.getParcelable<Serving>(Constant.serving)

                serving?.let {
                    servings.add(serving);
                    servingsIds.add(serving.id.toString())
                    addedservingsadapter.notifyItemInserted(servings.size)
                }

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

            switch1.setOnCheckedChangeListener { _, ischecked ->
                if (ischecked)
                    checkLocationPermissionAndGetCurrentLocation()
                else
                    switch1.text = "Am where it is"

                editTextText3.isEnabled = ischecked
            }

            button4.setOnClickListener {

                //check for prefilled
                if (prefilledIds.isNotEmpty()) {
                    println("all ${allgemsservings.size}")
                    println("prefsize ${prefilledIds.size}")
                    println("prefs $prefilledIds")
                    println("all $allgemsservings")
                    println("prefsmapped ${prefilledIds.map { allgemsservings[it].id }}")

                    servingsIds.addAll(prefilledIds.map { allgemsservings[it].id.toString() })
                    servings.addAll(prefilledIds.map { allgemsservings[it] })
                }


                val dialog = LoadingDialog.newInstance("saving gem.")


                if (checkFields()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val uid = appDatastore.userId.first()
                        uid?.let { userId ->

                            val gem = GemDto(
                                placeName = editTextText2.text.toString(),
                                gemId = null,
                                offerings = servingsIds.toList(),
                                servings = servings,
                                images = images.toList(),
                                addedBy = userId,
                                latLng = latLng?.toStringJson(),
                                locationName = editTextText3.text.toString()
                            )

                            gemsViewModel.addGem(gem) { booleanResource ->
                                when (booleanResource) {
                                    is Resource.Error -> {
                                        dialog.dismiss()
                                        requireContext().showToast(booleanResource.message.toString())
                                    }

                                    is Resource.Loading -> {
                                        dialog.show(parentFragmentManager, null)
                                    }

                                    is Resource.Success -> {
                                        dialog.dismiss()
                                        findNavController().navigateUp()
                                    }
                                }
                            }
                        }?:requireContext().showToast("Something unexpected happened.")
                    }

                }
            }

            loadingFeaturesAdd.stopShimmer()
            loadingAddPlaceImages.stopShimmer()
        }
    }

    private fun checkLocationPermissionAndGetCurrentLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    it?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                        binding.switch1.text = latLng!!.getAdress(requireContext())
                    }
                }
            }

            else -> {
                // Request location permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun FragmentAddGemBinding.checkFields(): Boolean {

        if (editTextText2.text.isEmpty()) {
            requireContext().showToast("provide a place name")
            return false
        }

        if (!switch1.isChecked && editTextText3.text.isEmpty()) {
            requireContext().showToast("we need a location")
            return false
        }

        return true
    }


    private fun checkPermissionAndPickImage() {
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
                launchImagePicker()
            }

            else -> {
                // Request the permission
                permissionLauncher.launch(permission)
            }
        }
    }

    fun launchImagePicker() {
        imagePickerLauncher.launch("image/*")
    }


}