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
import io.github.junrdev.hiddengems.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

            toolbar2.apply {
                title = gem.placeName
//                subtitle = gem.l
                setNavigationOnClickListener { findNavController().navigateUp() }
            }

            loadingReviews.apply { startShimmer() }
            loadingPlaceImages.apply { startShimmer() }
            loadingPlaceFeatures.apply { startShimmer() }

            CoroutineScope(Dispatchers.Main).launch {

                loadingReviews.apply { startShimmer() }
                loadingPlaceImages.apply { startShimmer() }
                loadingPlaceFeatures.apply { startShimmer() }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.savemenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}