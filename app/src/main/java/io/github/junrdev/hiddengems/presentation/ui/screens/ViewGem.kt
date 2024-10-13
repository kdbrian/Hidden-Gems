package io.github.junrdev.hiddengems.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.databinding.FragmentViewGemBinding
import io.github.junrdev.hiddengems.presentation.adapter.ImagesListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ReviewListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.getAdress
import io.github.junrdev.hiddengems.presentation.ui.jsonToLatLong
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.ReviewViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ViewGem : Fragment() {

    lateinit var gem: Gem
    lateinit var binding: FragmentViewGemBinding
    private val reviewViewModel by viewModels<ReviewViewModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var appDatastore: AppDatastore

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            gem.latLng?.let {
//                getCurrentLocationAndOpenMaps(it)
//            }
        } else {
            // Handle permission denial if needed
            return@registerForActivityResult
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentViewGemBinding.inflate(inflater, container, false).also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar2)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())


        binding.apply {

            gem = arguments?.getParcelable(Constant.gem) ?: Gem()
            place = gem

            toolbar2.apply {
                title = gem.placeName
                setNavigationOnClickListener { findNavController().navigateUp() }
            }

            loadingReviews.apply { startShimmer() }
            loadingPlaceImages.apply { startShimmer() }
            loadingPlaceFeatures.apply { startShimmer() }

            CoroutineScope(Dispatchers.Main).launch {
                if (gem.images.isNotEmpty()) {
                    delay(300)
                    loadingPlaceImages.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }

                    imagesList.apply {
                        visibility = View.VISIBLE
                        val snapHelper = CarouselSnapHelper()
                        adapter = ImagesListAdapter(requireContext(), gem.images)
                        layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
                        snapHelper.attachToRecyclerView(this)
                    }
                } else
                    loadingPlaceImages.stopShimmer()

                if (gem.latLng != null) {
                    //resolve location name from latlng
                    textView14.text = gem.latLng!!.jsonToLatLong().getAdress(requireContext())
                    locationIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.baseline_location_pin_24
                        )
                    )

                    viewInMap.setOnClickListener {
                        if (gem.latLng != null) {
                            checkLocationPermissionAndOpenMaps(gem.latLng!!.jsonToLatLong())
                        }
                    }
                } else {
                    textView14.text = gem.locationName
                    locationIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.round_location_off_24
                        )
                    )
                }

                if (gem.servings.isNotEmpty()) {
                    loadingPlaceFeatures.visibility = View.GONE
                    servingsList.apply {
                        visibility = View.VISIBLE
                        adapter = ServingListAdapter(gem.servings.toMutableList())
                    }
                } else
                    loadingPlaceFeatures.stopShimmer()

                reviewViewModel.getGemReviews(gemId = gem.gemId)
                reviewViewModel.gemreviews.observe(viewLifecycleOwner) { reviewsResource ->
                    println("resource $reviewsResource")
                    println("resourcedata ${reviewsResource.data?.size}")
                    println("resourceerror ${reviewsResource.message}")
                    when (reviewsResource) {
                        is Resource.Error -> Unit
                        is Resource.Loading -> Unit
                        is Resource.Success -> {
                            loadingReviews.stopShimmer()
                            if (!reviewsResource.data.isNullOrEmpty()) {
                                loadingReviews.visibility = View.GONE
                                reviewList.apply {
                                    visibility = View.VISIBLE
                                    adapter = ReviewListAdapter(reviewsResource.data)
                                }

                            }
                        }
                    }
                }

                textView15.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (appDatastore.isEmailVerified.first()) {
                            findNavController().navigate(
                                R.id.action_viewGem_to_rateDialog,
                                bundleOf("gem" to gem)
                            )
                        } else
                            requireContext().showToast("\uD83D\uDE09 Verify email first to comment.")
                    }
                }


            }

        }

    }

    private fun checkLocationPermissionAndOpenMaps(latLng: LatLng) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, get location and open maps
                getCurrentLocationAndOpenMaps(latLng)
            }

            else -> {
                // Request location permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocationAndOpenMaps(latLng: LatLng) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    openGoogleMaps(
                        it.latitude,
                        it.longitude,
                        latLng.latitude,
                        latLng.longitude
                    )
                }
            }
    }

    private fun openGoogleMaps(
        currentLat: Double,
        currentLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ) {
        val uri =
            Uri.parse("http://maps.google.com/maps?saddr=$currentLat,$currentLng&daddr=$destinationLat,$destinationLng")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        intent.resolveActivity(requireActivity().packageManager)?.let {
            startActivity(intent)
        }
    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.savemenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    companion object {
        val pictureDimens = listOf(
            Pair(4, 5),//ratio 4:5
        )
    }
}