package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.FragmentHomeScreenBinding


@AndroidEntryPoint
class HomeScreen : Fragment() {

    lateinit var binding: FragmentHomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeScreenBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            loadingForYou.apply { startShimmer() }
            loadingTopPicks.apply { startShimmer() }
            loadingTrendingPlaces.apply { startShimmer() }

            editTextText.setOnClickListener {
                findNavController().navigate(R.id.action_homeScreen_to_searchResults)
            }


            addGem.setOnClickListener {
                findNavController().navigate(R.id.action_homeScreen_to_addGem)
            }

            textView2.setOnClickListener {
                findNavController().navigate(R.id.action_homeScreen_to_viewGem)
            }

        }
    }
}