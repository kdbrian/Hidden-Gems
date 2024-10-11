package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.databinding.FragmentViewGemBinding
import io.github.junrdev.hiddengems.presentation.adapter.ImagesListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.ui.getLocationName
import io.github.junrdev.hiddengems.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewGem : Fragment() {

    lateinit var gem: Gem
    lateinit var binding: FragmentViewGemBinding

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
                    val count = gem.images.size
                    delay(300)
                    loadingPlaceImages.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }

                    imagesList.apply {
                        visibility = View.VISIBLE
                        adapter = ImagesListAdapter(requireContext(), gem.images)
                    }
                } else
                    loadingPlaceImages.stopShimmer()

                if (gem.latLng != null) {
                    //resolve location name from latlng
                    textView14.text = requireContext().getLocationName(
                        gem.latLng!!.latitude,
                        gem.latLng!!.longitude
                    )
                } else
                    textView14.text = gem.locationName

                if (gem.servings.isNotEmpty()) {
                    loadingPlaceFeatures.visibility = View.GONE
                    servingsList.adapter = ServingListAdapter(gem.servings.toMutableList())
                } else
                    loadingPlaceFeatures.stopShimmer()


                if (gem.reviews.isNotEmpty()) {
                    loadingReviews.visibility = View.GONE


                } else
                    loadingReviews.stopShimmer()

            }
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