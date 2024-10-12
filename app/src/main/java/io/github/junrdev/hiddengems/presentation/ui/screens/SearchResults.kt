package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Serving
import io.github.junrdev.hiddengems.databinding.FragmentSearchResultsBinding
import io.github.junrdev.hiddengems.presentation.adapter.PlaceListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.GemsViewModel
import io.github.junrdev.hiddengems.presentation.viewmodel.ServingsViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResults : Fragment() {

    lateinit var binding: FragmentSearchResultsBinding
    private val gemsViewModel by viewModels<GemsViewModel>()
    private val servingsViewModel by viewModels<ServingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSearchResultsBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.apply {

            arguments?.let { args ->
                val name = args.getString("name")
                val serving = args.getParcelable<Serving>(Constant.serving)

//                name?.let {
//                    println("name $it")
//                    toolbar.title = it
//                    gemsViewModel.searchGemsByServing(it)
//                }

                serving?.let {
                    println("serving $it")
                    gemsViewModel.searchGemsByServing(it)
                    toolbar.title = it.name
                }


                CoroutineScope(Dispatchers.Main).launch {
                    gemsViewModel.searchedgems.observe(viewLifecycleOwner) { searchResultsResource ->

                        when (searchResultsResource) {
                            is Resource.Error -> {
                                loadingSearchResults.stopShimmer()
                                requireContext().showToast("Failed due to ${searchResultsResource.message}")
                            }

                            is Resource.Loading -> {
                                loadingSearchResults.apply {
                                    startShimmer()
                                    visibility = View.VISIBLE
                                }
                                searchResults.visibility = View.GONE
                            }

                            is Resource.Success -> {
                                loadingSearchResults.stopShimmer()
                                searchResultsResource.data?.let { gemList ->
                                    if (gemList.isNotEmpty()){
                                        textView13.text = "${gemList.size} gems"
                                        loadingSearchResults.visibility = View.GONE
                                        searchResults.visibility = View.VISIBLE
                                        searchResults.adapter =
                                            PlaceListAdapter(requireContext(), gemList) {
                                                this@SearchResults.findNavController().navigate(
                                                    R.id.action_searchResults_to_viewGem,
                                                    bundleOf(Constant.gem to it)
                                                )
                                            }
                                    }else
                                        requireContext().showToast(" \uD83D\uDE43 missing matches found")
                                }
                            }
                        }
                    }
                }


            }
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filtermenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}