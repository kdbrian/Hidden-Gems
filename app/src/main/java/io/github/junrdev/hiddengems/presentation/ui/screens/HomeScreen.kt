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
import io.github.junrdev.hiddengems.presentation.viewmodel.GemsViewModel
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeScreen : Fragment() {

    lateinit var binding: FragmentHomeScreenBinding
    private val gemsViewModel by viewModels<GemsViewModel>()

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
                            println("data ${gemsResource.data}")
                            loadingForYou.apply { stopShimmer() }
                            loadingTopPicks.apply { stopShimmer() }
                            loadingTrendingPlaces.apply { stopShimmer() }
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
                            bundleOf("query" to text.toString())
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