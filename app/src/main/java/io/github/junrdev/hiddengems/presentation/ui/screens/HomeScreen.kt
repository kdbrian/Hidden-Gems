package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.FragmentHomeScreenBinding
import io.github.junrdev.hiddengems.presentation.adapter.PlaceListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.viewmodel.GemsViewModel
import io.github.junrdev.hiddengems.presentation.viewmodel.ServingsViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeScreen : Fragment() {

    lateinit var binding: FragmentHomeScreenBinding
    private val gemsViewModel by viewModels<GemsViewModel>()
    private val servingsViewModel by viewModels<ServingsViewModel>()

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


            CoroutineScope(Dispatchers.Main).launch {

                gemsViewModel.gems.observe(viewLifecycleOwner) { gemsResource ->

                    when (gemsResource) {

                        is Resource.Error -> {
                            loadingForYou.apply { stopShimmer() }
                            loadingTopPicks.apply { stopShimmer() }
                            loadingTrendingPlaces.apply { stopShimmer() }
                        }

                        is Resource.Loading -> {
                            loadingForYou.apply { startShimmer() }
                            loadingTopPicks.apply { startShimmer() }
                            loadingTrendingPlaces.apply { startShimmer() }
                        }

                        is Resource.Success -> {
                            val places = gemsResource.data
                            println("data $places")
                            loadingTrendingPlaces.apply { stopShimmer() }
                            loadingForYou.apply { stopShimmer() }
                            places?.let {
                                loadingTopPicks.apply { stopShimmer(); visibility = View.GONE }
                                topPicks.apply {
                                    visibility = View.VISIBLE
                                    adapter = PlaceListAdapter(requireContext(), places) {
                                        findNavController().navigate(
                                            R.id.action_homeScreen_to_viewGem,
                                            bundleOf(Constant.gem to it)
                                        )
                                    }
                                }
                            }

                        }
                    }
                }

            }

            CoroutineScope(Dispatchers.Main).launch {
                servingsViewModel.servings.observe(viewLifecycleOwner) { servingsResource ->
                    when (servingsResource) {
                        is Resource.Error -> {
                            loadingServings.stopShimmer()
                        }

                        is Resource.Loading -> {
                            loadingServings.apply {
                                visibility = View.VISIBLE
                                startShimmer()
                            }
                            servings.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            loadingServings.apply {
                                stopShimmer()
                                visibility = View.GONE
                            }
                            servingsResource.data?.let { servingList ->
                                servings.adapter = ServingListAdapter(servingList.toMutableList()){
                                    findNavController().navigate(
                                        R.id.action_homeScreen_to_searchResults,
                                        bundleOf(Constant.serving to it.name.toString())
                                    )
                                }
                                servings.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

            editTextText.apply {
                imeOptions = EditorInfo.IME_ACTION_SEARCH

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        findNavController().navigate(
                            R.id.action_homeScreen_to_searchResults,
                            bundleOf("name" to text.toString())
                        )
                        true
                    }
                    false
                }
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